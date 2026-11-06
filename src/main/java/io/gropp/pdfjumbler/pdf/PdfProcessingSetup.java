package io.gropp.pdfjumbler.pdf;

import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.I18nUtil;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.jspecify.annotations.Nullable;
import org.tinylog.Logger;

final class PdfProcessingSetup {
    private static final int EXIT_NO_EDITOR = 101;
    private static final int EXIT_NO_RENDERER = 102;
    private static final int EXIT_PLUGIN_ERROR = 103;

    private PdfProcessingSetup() {}

    static PdfProcessingFactory createFactory() {
        ResourceBundle resources = PdfJumblerResources.getResources();
        PluginDiscovery discovery = new PluginDiscovery(
                List.of(io.gropp.pdfjumbler.pdfbox.PdfEditor.class),
                List.of(io.gropp.pdfjumbler.pdfbox.PdfRenderer.class));

        File pluginPath = getPluginPath();
        if (pluginPath == null) {
            Logger.error("No plugin path found.");
        } else {
            Logger.info("Plugin path: {}", pluginPath);
            try {
                discovery.discover(pluginPath);
            } catch (PluginDiscovery.PluginLoadException e) {
                Logger.error(e);
                String message = String.format(
                        "%s: %s",
                        String.format(
                                resources.getString(I18nKeys.PLUGIN_INIT_ERROR),
                                e.getJarFile().getAbsolutePath()),
                        localizedMessage(resources, e));
                exitWithError(message, resources.getString(I18nKeys.PLUGIN_ERROR_TITLE), EXIT_PLUGIN_ERROR);
            }
        }

        try {
            return new PdfProcessingFactory(
                    discovery.getEditorClasses(),
                    discovery.getRendererClasses(),
                    Preferences.userNodeForPackage(PdfJumbler.class),
                    System.getProperty("pdfjumbler.editor"),
                    System.getProperty("pdfjumbler.renderer"));
        } catch (NoPdfProcessorException e) {
            Logger.error(e);
            if (e.getKind() == NoPdfProcessorException.Kind.EDITOR) {
                exitWithError(
                        resources.getString(I18nKeys.NO_PDF_EDITOR_TEXT),
                        resources.getString(I18nKeys.NO_PDF_EDITOR_TITLE),
                        EXIT_NO_EDITOR);
            } else {
                exitWithError(
                        resources.getString(I18nKeys.NO_PDF_RENDERER_TEXT),
                        resources.getString(I18nKeys.NO_PDF_RENDERER_TITLE),
                        EXIT_NO_RENDERER);
            }
            throw new AssertionError(e);
        }
    }

    private static String localizedMessage(ResourceBundle resources, Exception e) {
        String message = e.getMessage();
        return message == null ? e.getClass().getSimpleName() : I18nUtil.getString(resources, message, message);
    }

    private static void exitWithError(String message, String title, int status) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        System.exit(status);
    }

    private static @Nullable File getPluginPath() {
        String userPluginPath = System.getProperty("pdfjumbler.pluginpath");
        if (userPluginPath != null) {
            return new File(userPluginPath);
        }

        try {
            String cls = "/" + PdfProcessingSetup.class.getName().replace('.', '/') + ".class";
            URL url = PdfProcessingSetup.class.getResource(cls);
            if (url.getProtocol().equals("jar")) {
                String path = url.getPath();
                URI jarUri = URI.create(path.substring(0, path.indexOf('!')));
                return "file".equals(jarUri.getScheme()) ? new File(jarUri).getParentFile() : null;
            } else if (url.getProtocol().equals("file")) {
                String path = url.getPath();
                return path.endsWith(cls) ? new File(path.substring(0, path.length() - cls.length())) : null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
