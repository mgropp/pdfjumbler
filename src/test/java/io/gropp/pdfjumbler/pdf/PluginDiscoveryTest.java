package io.gropp.pdfjumbler.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import io.gropp.pdfjumbler.PdfJumbler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PluginDiscoveryTest {
	private static final Class<? extends PdfEditor> BUILT_IN_EDITOR = io.gropp.pdfjumbler.pdfbox.PdfEditor.class;
	private static final Class<? extends PdfRenderer> BUILT_IN_RENDERER = io.gropp.pdfjumbler.pdfbox.PdfRenderer.class;

	@TempDir
	File pluginDir;

	private final PluginDiscovery discovery = new PluginDiscovery(List.of(BUILT_IN_EDITOR), List.of(BUILT_IN_RENDERER));

	private void writePlugin(String name, String version, String editors, String renderers) throws IOException {
		Properties properties = new Properties();
		properties.setProperty(Plugin.REQUIRED_VERSION_KEY, version);
		if (editors != null) {
			properties.setProperty(Plugin.PDF_EDITOR_KEY, editors);
		}
		if (renderers != null) {
			properties.setProperty(Plugin.PDF_RENDERER_KEY, renderers);
		}
		try (JarOutputStream out = new JarOutputStream(new FileOutputStream(new File(pluginDir, name)))) {
			out.putNextEntry(new ZipEntry("pdfjumbler-plugin.properties"));
			properties.store(out, null);
			out.closeEntry();
		}
	}

	private static String currentVersion() {
		return Integer.toString(PdfJumbler.VERSION);
	}

	@Test
	void startsWithBuiltInProcessors() {
		assertThat(discovery.getEditorClasses()).containsExactly(BUILT_IN_EDITOR);
		assertThat(discovery.getRendererClasses()).containsExactly(BUILT_IN_RENDERER);
	}

	@Test
	void appendsPluginClassesInJarNameOrder() throws Exception {
		writePlugin("b.jar", currentVersion(), PdfProcessingFactoryTest.EditorB.class.getName(), null);
		writePlugin("a.jar", currentVersion(), PdfProcessingFactoryTest.EditorA.class.getName(),
				PdfProcessingFactoryTest.RendererA.class.getName());

		discovery.discover(pluginDir);

		assertThat(discovery.getEditorClasses()).containsExactly(
				BUILT_IN_EDITOR, PdfProcessingFactoryTest.EditorA.class, PdfProcessingFactoryTest.EditorB.class);
		assertThat(discovery.getRendererClasses()).containsExactly(
				BUILT_IN_RENDERER, PdfProcessingFactoryTest.RendererA.class);
	}

	@Test
	void ignoresNonJarFilesAndJarsWithoutPluginProperties() throws Exception {
		Files.writeString(new File(pluginDir, "notes.txt").toPath(), "not a plugin");
		new JarOutputStream(new FileOutputStream(new File(pluginDir, "library.jar"))).close();

		discovery.discover(pluginDir);

		assertThat(discovery.getEditorClasses()).containsExactly(BUILT_IN_EDITOR);
	}

	@Test
	void ignoresMissingDirectory() throws Exception {
		discovery.discover(new File(pluginDir, "missing"));

		assertThat(discovery.getEditorClasses()).containsExactly(BUILT_IN_EDITOR);
	}

	@Test
	void reportsBrokenPluginWithItsJarFile() throws Exception {
		writePlugin("broken.jar", currentVersion(), "com.example.Missing", null);

		assertThatThrownBy(() -> discovery.discover(pluginDir))
				.isInstanceOfSatisfying(PluginDiscovery.PluginLoadException.class,
						e -> assertThat(e.getJarFile()).hasName("broken.jar"));
	}
}
