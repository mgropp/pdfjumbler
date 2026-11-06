package io.gropp.pdfjumbler.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import io.gropp.pdfjumbler.ConfigKeys;
import io.gropp.pdfjumbler.InMemoryPreferences;
import org.junit.jupiter.api.Test;

class PdfProcessingFactoryTest {
	private final Preferences preferences = new InMemoryPreferences();

	private PdfProcessingFactory factory(String requestedEditor, String requestedRenderer) throws NoPdfProcessorException {
		return new PdfProcessingFactory(
				List.of(EditorA.class, EditorB.class),
				List.of(RendererA.class, RendererB.class),
				preferences,
				requestedEditor,
				requestedRenderer);
	}

	@Test
	void selectsFirstAvailableProcessorsByDefault() throws Exception {
		PdfProcessingFactory factory = factory(null, null);

		assertThat(factory.getEditor()).isInstanceOf(EditorA.class);
		assertThat(factory.getRenderer()).isInstanceOf(RendererA.class);
	}

	@Test
	void prefersStoredPreference() throws Exception {
		preferences.put(ConfigKeys.EDITOR, EditorB.class.getName());
		preferences.put(ConfigKeys.RENDERER, RendererB.class.getName());

		PdfProcessingFactory factory = factory(null, null);

		assertThat(factory.getEditor()).isInstanceOf(EditorB.class);
		assertThat(factory.getRenderer()).isInstanceOf(RendererB.class);
	}

	@Test
	void requestedClassOverridesStoredPreference() throws Exception {
		preferences.put(ConfigKeys.EDITOR, EditorA.class.getName());

		PdfProcessingFactory factory = factory(EditorB.class.getName(), RendererB.class.getName());

		assertThat(factory.getEditor()).isInstanceOf(EditorB.class);
		assertThat(factory.getRenderer()).isInstanceOf(RendererB.class);
	}

	@Test
	void ignoresUnknownRequestedClass() throws Exception {
		PdfProcessingFactory factory = factory("com.example.Unknown", null);

		assertThat(factory.getEditor()).isInstanceOf(EditorA.class);
	}

	@Test
	void skipsClassesThatCannotBeInstantiated() throws Exception {
		PdfProcessingFactory factory = new PdfProcessingFactory(
				List.of(BrokenEditor.class, EditorB.class), List.of(RendererA.class), preferences, null, null);

		assertThat(factory.getEditor()).isInstanceOf(EditorB.class);
	}

	@Test
	void failsWithoutUsableEditor() {
		assertThatThrownBy(() -> new PdfProcessingFactory(
						List.of(BrokenEditor.class), List.of(RendererA.class), preferences, null, null))
				.isInstanceOfSatisfying(NoPdfProcessorException.class,
						e -> assertThat(e.getKind()).isEqualTo(NoPdfProcessorException.Kind.EDITOR));
	}

	@Test
	void failsWithoutUsableRenderer() {
		assertThatThrownBy(() -> new PdfProcessingFactory(
						List.of(EditorA.class), List.of(), preferences, null, null))
				.isInstanceOfSatisfying(NoPdfProcessorException.class,
						e -> assertThat(e.getKind()).isEqualTo(NoPdfProcessorException.Kind.RENDERER));
	}

	@Test
	void changingEditorNotifiesListenersAndStoresPreference() throws Exception {
		PdfProcessingFactory factory = factory(null, null);
		PdfEditor oldEditor = factory.getEditor();
		RecordingListener listener = new RecordingListener();
		factory.addProcessorListener(listener);

		factory.setEditorClass(EditorB.class);

		assertThat(factory.getEditorClass()).isEqualTo(EditorB.class);
		assertThat(listener.oldEditors).containsExactly(oldEditor);
		assertThat(preferences.get(ConfigKeys.EDITOR, null)).isEqualTo(EditorB.class.getName());
	}

	@Test
	void changingRendererNotifiesListenersAndStoresPreference() throws Exception {
		PdfProcessingFactory factory = factory(null, null);
		PdfRenderer oldRenderer = factory.getRenderer();
		RecordingListener listener = new RecordingListener();
		factory.addProcessorListener(listener);

		factory.setRendererClass(RendererB.class);

		assertThat(factory.getRendererClass()).isEqualTo(RendererB.class);
		assertThat(listener.oldRenderers).containsExactly(oldRenderer);
		assertThat(preferences.get(ConfigKeys.RENDERER, null)).isEqualTo(RendererB.class.getName());
	}

	@Test
	void removedListenerIsNotNotified() throws Exception {
		PdfProcessingFactory factory = factory(null, null);
		RecordingListener listener = new RecordingListener();
		factory.addProcessorListener(listener);
		factory.removeProcessorListener(listener);

		factory.setEditorClass(EditorB.class);

		assertThat(listener.oldEditors).isEmpty();
	}

	@Test
	void listsAvailableProcessors() throws Exception {
		PdfProcessingFactory factory = factory(null, null);

		assertThat(factory.getAvailableEditors()).containsExactly(EditorA.class, EditorB.class);
		assertThat(factory.getAvailableRenderers()).containsExactly(RendererA.class, RendererB.class);
	}

	@Test
	void friendlyNameUsesStaticMethodOrFallsBackToClassName() {
		assertThat(PdfProcessingFactory.getFriendlyName(EditorA.class)).isEqualTo("Editor A");
		assertThat(PdfProcessingFactory.getFriendlyName(EditorB.class)).isEqualTo(EditorB.class.getCanonicalName());
	}

	public static class EditorA implements PdfEditor {
		public static String getFriendlyName() {
			return "Editor A";
		}

		@Override
		public void saveDocument(List<Page> pages, File file) {}

		@Override
		public void close() {}
	}

	public static class EditorB extends EditorA {
		public static String getFriendlyName() {
			throw new UnsupportedOperationException();
		}
	}

	public static class BrokenEditor extends EditorA {
		public BrokenEditor() {
			throw new IllegalStateException();
		}
	}

	public static class RendererA implements PdfRenderer {
		@Override
		public Image renderPage(Page page, int maxWidth, int maxHeight) {
			return null;
		}

		@Override
		public int getNumberOfPages(File file) {
			return 0;
		}

		@Override
		public void close() {}
	}

	public static class RendererB extends RendererA {}

	private static final class RecordingListener implements PdfProcessorListener {
		final List<PdfEditor> oldEditors = new ArrayList<>();
		final List<PdfRenderer> oldRenderers = new ArrayList<>();

		@Override
		public void pdfEditorChanged(PdfEditor oldEditor, PdfEditor newEditor) {
			oldEditors.add(oldEditor);
		}

		@Override
		public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer) {
			oldRenderers.add(oldRenderer);
		}
	}
}
