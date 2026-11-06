package io.gropp.pdfjumbler.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.PdfJumbler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PluginTest {
    private static final String EDITOR = io.gropp.pdfjumbler.pdfbox.PdfEditor.class.getName();
    private static final String RENDERER = io.gropp.pdfjumbler.pdfbox.PdfRenderer.class.getName();

    @TempDir
    File tempDir;

    private Locale previousLocale;

    @BeforeEach
    void useEnglishLocale() {
        previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(previousLocale);
    }

    private File jarWith(@Nullable Properties properties) throws IOException {
        File jar = new File(tempDir, "plugin.jar");
        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(jar))) {
            if (properties != null) {
                out.putNextEntry(new ZipEntry("pdfjumbler-plugin.properties"));
                properties.store(out, null);
                out.closeEntry();
            }
        }
        return jar;
    }

    private static Properties properties(String... keysAndValues) {
        Properties properties = new Properties();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            properties.setProperty(keysAndValues[i], keysAndValues[i + 1]);
        }
        return properties;
    }

    private static String currentVersion() {
        return Integer.toString(PdfJumbler.VERSION);
    }

    @Test
    void loadsEditorAndRendererClasses() throws Exception {
        Plugin plugin = new Plugin(jarWith(properties(
                Plugin.REQUIRED_VERSION_KEY, currentVersion(),
                Plugin.PDF_EDITOR_KEY, EDITOR,
                Plugin.PDF_RENDERER_KEY, RENDERER)));

        assertThat(plugin.hasPdfEditors()).isTrue();
        assertThat(plugin.hasPdfRenderers()).isTrue();
        assertThat(plugin.getPdfEditorClasses()).containsExactly(io.gropp.pdfjumbler.pdfbox.PdfEditor.class);
        assertThat(plugin.getPdfRendererClasses()).containsExactly(io.gropp.pdfjumbler.pdfbox.PdfRenderer.class);
    }

    @Test
    void loadsCommaSeparatedClassList() throws Exception {
        Plugin plugin = new Plugin(jarWith(properties(
                Plugin.REQUIRED_VERSION_KEY, currentVersion(), Plugin.PDF_EDITOR_KEY, EDITOR + "," + EDITOR)));

        assertThat(plugin.getPdfEditorClasses()).hasSize(2);
    }

    @Test
    void pluginWithOnlyEditorsHasNoRenderers() throws Exception {
        Plugin plugin = new Plugin(
                jarWith(properties(Plugin.REQUIRED_VERSION_KEY, currentVersion(), Plugin.PDF_EDITOR_KEY, EDITOR)));

        assertThat(plugin.hasPdfRenderers()).isFalse();
        assertThat(plugin.getPdfRendererClasses()).isEmpty();
    }

    @Test
    void rejectsJarWithoutPluginProperties() throws IOException {
        File jar = jarWith(null);

        assertThatThrownBy(() -> new Plugin(jar)).isInstanceOf(IOException.class);
    }

    @Test
    void rejectsPropertiesWithoutPluginClasses() throws IOException {
        File jar = jarWith(properties(Plugin.REQUIRED_VERSION_KEY, currentVersion()));

        assertThatThrownBy(() -> new Plugin(jar)).isInstanceOf(IOException.class);
    }

    @Test
    void rejectsMissingVersion() throws IOException {
        File jar = jarWith(properties(Plugin.PDF_EDITOR_KEY, EDITOR));

        assertThatThrownBy(() -> new Plugin(jar)).isInstanceOf(PluginException.class);
    }

    @Test
    void rejectsPluginRequiringNewerVersion() throws IOException {
        String newerVersion = Integer.toString(PdfJumbler.VERSION + 1);
        File jar = jarWith(properties(
                Plugin.REQUIRED_VERSION_KEY, newerVersion,
                Plugin.PDF_EDITOR_KEY, EDITOR));

        assertThatThrownBy(() -> new Plugin(jar))
                .isInstanceOf(PluginException.class)
                .hasMessageContaining(newerVersion);
    }

    @Test
    void rejectsUnknownClass() throws IOException {
        File jar = jarWith(properties(
                Plugin.REQUIRED_VERSION_KEY, currentVersion(), Plugin.PDF_RENDERER_KEY, "com.example.Missing"));

        assertThatThrownBy(() -> new Plugin(jar)).isInstanceOf(PluginException.class);
    }
}
