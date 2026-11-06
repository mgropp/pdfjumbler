package io.gropp.pdfjumbler.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

public final class PluginDiscovery {
	private final List<Class<? extends PdfEditor>> editorClasses = new ArrayList<>();
	private final List<Class<? extends PdfRenderer>> rendererClasses = new ArrayList<>();

	public PluginDiscovery(List<Class<? extends PdfEditor>> builtInEditors, List<Class<? extends PdfRenderer>> builtInRenderers) {
		editorClasses.addAll(builtInEditors);
		rendererClasses.addAll(builtInRenderers);
	}

	public void discover(File pluginDirectory) throws PluginLoadException {
		File[] jarFiles = pluginDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
		if (jarFiles == null) {
			return;
		}
		Arrays.sort(jarFiles);

		for (File jarFile : jarFiles) {
			Logger.info("Attempting to load plugin: {}", jarFile);
			Plugin plugin;
			try {
				plugin = new Plugin(jarFile);
			} catch (IOException e) {
				Logger.info("Ignoring plugin {}: {}", jarFile, e);
				continue;
			} catch (PluginException e) {
				throw new PluginLoadException(jarFile, e);
			}
			editorClasses.addAll(plugin.getPdfEditorClasses());
			rendererClasses.addAll(plugin.getPdfRendererClasses());
		}
	}

	public List<Class<? extends PdfEditor>> getEditorClasses() {
		return List.copyOf(editorClasses);
	}

	public List<Class<? extends PdfRenderer>> getRendererClasses() {
		return List.copyOf(rendererClasses);
	}

	public static final class PluginLoadException extends Exception {
		private static final long serialVersionUID = 1L;

		private final File jarFile;

		PluginLoadException(File jarFile, PluginException cause) {
			super(cause.getMessage(), cause);
			this.jarFile = jarFile;
		}

		public File getJarFile() {
			return jarFile;
		}
	}
}
