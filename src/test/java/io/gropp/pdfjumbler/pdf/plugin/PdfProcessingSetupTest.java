package io.gropp.pdfjumbler.pdf.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactoryTest;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import io.gropp.pdfjumbler.pdfbox.PdfBoxEditor;
import io.gropp.pdfjumbler.pdfbox.PdfBoxRenderer;
import io.gropp.pdfjumbler.testutil.InMemoryPreferences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfProcessingSetupTest {
    private static final List<Class<? extends PdfEditor>> BUILT_IN_EDITORS = List.of(PdfBoxEditor.class);
    private static final List<Class<? extends PdfRenderer>> BUILT_IN_RENDERERS = List.of(PdfBoxRenderer.class);

    @TempDir
    File pluginDir;

    private final RecordingStartupErrors errors = new RecordingStartupErrors();

    private record FatalError(String message, String title, int status) {}

    private static final class StartupAborted extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static final class RecordingStartupErrors implements StartupErrors {
        final List<FatalError> fatalErrors = new ArrayList<>();

        @Override
        public void fatal(String message, String title, int exitStatus) {
            fatalErrors.add(new FatalError(message, title, exitStatus));
            throw new StartupAborted();
        }
    }

    private PdfProcessingFactory createFactory(
            @Nullable File pluginPath,
            List<Class<? extends PdfEditor>> editors,
            List<Class<? extends PdfRenderer>> renderers,
            @Nullable String requestedEditor) {
        return PdfProcessingSetup.createFactory(
                errors, pluginPath, editors, renderers, new InMemoryPreferences(), requestedEditor, null);
    }

    private void writePlugin(String name, @Nullable String editors) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(Plugin.REQUIRED_VERSION_KEY, Integer.toString(PluginApi.VERSION));
        if (editors != null) {
            properties.setProperty(Plugin.PDF_EDITOR_KEY, editors);
        }
        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(new File(pluginDir, name)))) {
            out.putNextEntry(new ZipEntry("pdfjumbler-plugin.properties"));
            properties.store(out, null);
            out.closeEntry();
        }
    }

    @Test
    void usesBuiltInProcessorsWithoutPluginPath() {
        PdfProcessingFactory factory = createFactory(null, BUILT_IN_EDITORS, BUILT_IN_RENDERERS, null);

        assertThat(factory.getEditor()).isInstanceOf(PdfBoxEditor.class);
        assertThat(factory.getRenderer()).isInstanceOf(PdfBoxRenderer.class);
        assertThat(errors.fatalErrors).isEmpty();
    }

    @Test
    void makesPluginProcessorsSelectable() throws Exception {
        writePlugin("plugin.jar", PdfProcessingFactoryTest.EditorB.class.getName());

        PdfProcessingFactory factory = createFactory(
                pluginDir, BUILT_IN_EDITORS, BUILT_IN_RENDERERS, PdfProcessingFactoryTest.EditorB.class.getName());

        assertThat(factory.getEditor()).isInstanceOf(PdfProcessingFactoryTest.EditorB.class);
        assertThat(errors.fatalErrors).isEmpty();
    }

    @Test
    void brokenPluginIsFatalAndNamesTheJar() throws Exception {
        writePlugin("broken.jar", "com.example.Missing");

        assertThatThrownBy(() -> createFactory(pluginDir, BUILT_IN_EDITORS, BUILT_IN_RENDERERS, null))
                .isInstanceOf(StartupAborted.class);

        assertThat(errors.fatalErrors).singleElement().satisfies(error -> {
            assertThat(error.status()).isEqualTo(PdfProcessingSetup.EXIT_PLUGIN_ERROR);
            assertThat(error.message())
                    .contains(new File(pluginDir, "broken.jar").getAbsolutePath())
                    .contains("Failed to load editor class from plugin!");
        });
    }

    @Test
    void missingEditorIsFatal() {
        assertThatThrownBy(() -> createFactory(null, List.of(), BUILT_IN_RENDERERS, null))
                .isInstanceOf(StartupAborted.class);

        assertThat(errors.fatalErrors).singleElement().satisfies(error -> assertThat(error.status())
                .isEqualTo(PdfProcessingSetup.EXIT_NO_EDITOR));
    }

    @Test
    void missingRendererIsFatal() {
        assertThatThrownBy(() -> createFactory(null, BUILT_IN_EDITORS, List.of(), null))
                .isInstanceOf(StartupAborted.class);

        assertThat(errors.fatalErrors).singleElement().satisfies(error -> assertThat(error.status())
                .isEqualTo(PdfProcessingSetup.EXIT_NO_RENDERER));
    }
}
