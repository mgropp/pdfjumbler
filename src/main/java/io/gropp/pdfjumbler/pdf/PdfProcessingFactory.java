package io.gropp.pdfjumbler.pdf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.Preferences;

import io.gropp.pdfjumbler.ConfigKeys;
import org.tinylog.Logger;

public final class PdfProcessingFactory {
	private static PdfProcessingFactory instance;

	public static synchronized PdfProcessingFactory getInstance() {
		if (instance == null) {
			instance = PdfProcessingSetup.createFactory();
		}
		return instance;
	}

	private final List<Class<? extends PdfEditor>> editorClasses;
	private final List<Class<? extends PdfRenderer>> rendererClasses;
	private final Preferences preferences;
	private final List<PdfProcessorListener> listeners = new CopyOnWriteArrayList<>();

	private PdfEditor editor;
	private PdfRenderer renderer;

	public PdfProcessingFactory(
			List<Class<? extends PdfEditor>> editorClasses,
			List<Class<? extends PdfRenderer>> rendererClasses,
			Preferences preferences,
			String requestedEditor,
			String requestedRenderer)
			throws NoPdfProcessorException {
		this.editorClasses = List.copyOf(editorClasses);
		this.rendererClasses = List.copyOf(rendererClasses);
		this.preferences = preferences;

		editor = instantiateFirst(candidates(
				this.editorClasses, requestedEditor, preferences.get(ConfigKeys.EDITOR, null)));
		if (editor == null) {
			throw new NoPdfProcessorException(NoPdfProcessorException.Kind.EDITOR);
		}

		renderer = instantiateFirst(candidates(
				this.rendererClasses, requestedRenderer, preferences.get(ConfigKeys.RENDERER, null)));
		if (renderer == null) {
			throw new NoPdfProcessorException(NoPdfProcessorException.Kind.RENDERER);
		}
	}

	private static <T> List<Class<? extends T>> candidates(
			List<Class<? extends T>> available, String requestedClassName, String preferredClassName) {
		List<Class<? extends T>> candidates = new ArrayList<>(available.size() + 2);
		findByName(available, requestedClassName).ifPresent(candidates::add);
		findByName(available, preferredClassName).ifPresent(candidates::add);
		candidates.addAll(available);
		return candidates;
	}

	private static <T> Optional<Class<? extends T>> findByName(
			List<Class<? extends T>> classes, String className) {
		return classes.stream().filter(cls -> cls.getName().equals(className)).findFirst();
	}

	private static <T> T instantiateFirst(List<Class<? extends T>> candidates) {
		for (Class<? extends T> cls : candidates) {
			Logger.info("Trying to instantiate {}", cls);
			try {
				return cls.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				Logger.error(e);
			}
		}
		return null;
	}

	public PdfEditor getEditor() {
		return editor;
	}

	public PdfRenderer getRenderer() {
		return renderer;
	}

	public Class<? extends PdfEditor> getEditorClass() {
		return editor.getClass();
	}

	public Class<? extends PdfRenderer> getRendererClass() {
		return renderer.getClass();
	}

	public void setEditorClass(Class<? extends PdfEditor> cls) throws ReflectiveOperationException {
		PdfEditor oldEditor = editor;
		editor = cls.getDeclaredConstructor().newInstance();
		for (PdfProcessorListener listener : listeners) {
			listener.pdfEditorChanged(oldEditor, editor);
		}
		preferences.put(ConfigKeys.EDITOR, cls.getName());
	}

	public void setRendererClass(Class<? extends PdfRenderer> cls) throws ReflectiveOperationException {
		PdfRenderer oldRenderer = renderer;
		renderer = cls.getDeclaredConstructor().newInstance();
		for (PdfProcessorListener listener : listeners) {
			listener.pdfRendererChanged(oldRenderer, renderer);
		}
		preferences.put(ConfigKeys.RENDERER, cls.getName());
	}

	public Collection<Class<? extends PdfEditor>> getAvailableEditors() {
		return Collections.unmodifiableCollection(editorClasses);
	}

	public Collection<Class<? extends PdfRenderer>> getAvailableRenderers() {
		return Collections.unmodifiableCollection(rendererClasses);
	}

	public void addProcessorListener(PdfProcessorListener listener) {
		listeners.add(listener);
	}

	public void removeProcessorListener(PdfProcessorListener listener) {
		listeners.remove(listener);
	}

	public static String getFriendlyName(Class<?> cls) {
		try {
			Method method = cls.getMethod("getFriendlyName");
			return (String) method.invoke(null);
		} catch (Exception e) {
			return cls.getCanonicalName();
		}
	}
}
