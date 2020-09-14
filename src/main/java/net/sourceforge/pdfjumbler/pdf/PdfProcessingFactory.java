package net.sourceforge.pdfjumbler.pdf;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.I18nUtil;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import org.tinylog.Logger;

/**
 * In der Hoffnung, dass es irgendwann einmal /eine/
 * Bibliothek gibt, die das alles kann...
 *
 * 2020: PdfBox <3 !
 * 
 * @author Martin Gropp
 */
public final class PdfProcessingFactory {
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());
	
	private static final List<Class<? extends PdfEditor>> pdfEditorClasses = new ArrayList<>();
	private static final List<Class<? extends PdfRenderer>> pdfRendererClasses = new ArrayList<>();

	// add plugins from main (could be empty)
	static {
		pdfEditorClasses.add(net.sourceforge.pdfjumbler.pdfbox.PdfEditor.class);
		pdfRendererClasses.add(net.sourceforge.pdfjumbler.pdfbox.PdfRenderer.class);
	}

	private static PdfEditor editor = null;
	private static PdfRenderer renderer = null;
	
	private static final LinkedList<PdfProcessorListener> listeners = new LinkedList<>();
	
	static {
		discoverPlugins();
		selectInitialEditor();
		selectInitialRenderer();
	}
	
	private static void discoverPlugins() {
		File pluginPath = getPluginPath();
		if (pluginPath == null) {
			Logger.error("No plugin path found.");
			return;
		}
		Logger.info("Plugin path: {}", pluginPath);
		
		File[] jarFiles = pluginPath.listFiles(
			new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			}
		);
		Arrays.sort(jarFiles);
		
		for (File jarFile : jarFiles) {
			Logger.info("Attempting to load plugin: {}", jarFile);
			Plugin plugin;
			try {
				plugin = new Plugin(jarFile);
			}
			catch (IOException e) {
				// Ignore this plugin
				Logger.info("Ignoring plugin {}: {}", jarFile, e);
				continue;
			}
			catch (PluginException e) {
				Logger.error(e);
				JOptionPane.showMessageDialog(
					null,
					String.format(
						"%s: %s",
						String.format(
							resources.getString(I18nKeys.PLUGIN_INIT_ERROR),
							jarFile.getAbsolutePath()
						),
						I18nUtil.getString(resources, e.getMessage(), e.getMessage())
					),
					resources.getString(I18nKeys.PLUGIN_ERROR_TITLE),
					JOptionPane.ERROR_MESSAGE
				);
				System.exit(103);
				return;
			}
			
			if (plugin.hasPdfEditors()) {
				pdfEditorClasses.addAll(
					plugin.getPdfEditorClasses()
				);
			}
			
			if (plugin.hasPdfRenderers()) {
				pdfRendererClasses.addAll(
					plugin.getPdfRendererClasses()
				);
			}
		}
	}
	
	private static File getPluginPath() {
		String userPluginPath = System.getProperty("pdfjumbler.pluginpath", null);
		if (userPluginPath != null) {
			return new File(userPluginPath);
		}
		
		try {
			String cls = "/" + PdfProcessingFactory.class.getName().replace('.', '/') + ".class";
			URL url = PdfProcessingFactory.class.getResource(cls);
			if (url.getProtocol().equals("jar")) {
				String path = url.getPath();
				path = path.substring(0, path.indexOf('!'));
				URL jarURL = new URL(path);
				if (jarURL.getProtocol().equals("file")) {
					return new File(jarURL.toURI()).getParentFile();
				} else {
					return null;
				}
			} else if (url.getProtocol().equals("file")) {
				String path = url.getPath();
				if (path.endsWith(cls)) {
					path = path.substring(0, path.length() - cls.length());
					return new File(path);
				} else {
					return null;
				}
			}
			
			return null;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private static Class<? extends PdfEditor> findEditorClass(String className) {
		if (className == null) {
			return null;
		}
		
		for (Class<? extends PdfEditor> cls : pdfEditorClasses) {
			if (cls.getName().equals(className)) {
				return cls;
			}
		}
		
		return null;
	}
	
	private static Class<? extends PdfRenderer> findRendererClass(String className) {
		if (className == null) {
			return null;
		}
		
		for (Class<? extends PdfRenderer> cls : pdfRendererClasses) {
			if (cls.getName().equals(className)) {
				return cls;
			}
		}
		
		return null;
	}
	
	private static void selectInitialEditor() {
		List<Class<? extends PdfEditor>> classes = new ArrayList<>(pdfEditorClasses.size() + 2);
		classes.add(findEditorClass(
			System.getProperty("pdfjumbler.editor", null)
		));
		classes.add(findEditorClass(
			Preferences.userNodeForPackage(PdfJumbler.class).get("editor", null)
		));
		classes.addAll(pdfEditorClasses);
		
		for (Class<? extends PdfEditor> cls : classes) {
			if (cls == null) {
				continue;
			}
			Logger.info("Trying to instantiate editor: " + cls + "... ");
			try {
				editor = cls.getDeclaredConstructor().newInstance();
			}
			catch (Exception e) {
				Logger.error(e);
				continue;
			}
			Logger.info("Created editor instance.");
			break;
		}
		if (editor == null) {
			Logger.error("No pdf editors found, exiting.");
			JOptionPane.showMessageDialog(
				null,
				resources.getString(I18nKeys.NO_PDF_EDITOR_TEXT),
				resources.getString(I18nKeys.NO_PDF_EDITOR_TITLE),
				JOptionPane.ERROR_MESSAGE
			);
			System.exit(101);
		}
	}
		
	private static void selectInitialRenderer() {
		List<Class<? extends PdfRenderer>> classes = new ArrayList<>(pdfRendererClasses.size() + 2);
		classes.add(findRendererClass(
			System.getProperty("pdfjumbler.renderer", null)
		));
		classes.add(findRendererClass(
			Preferences.userNodeForPackage(PdfJumbler.class).get("renderer", null)
		));
		classes.addAll(pdfRendererClasses);
		
		for (Class<? extends PdfRenderer> cls : classes) {
			if (cls == null) {
				continue;
			}
			
			Logger.info("Trying to instantiate renderer: " + cls + "... ");
			try {
				renderer = cls.getDeclaredConstructor().newInstance();
			}
			catch (Exception e) {
				Logger.error(e);
				continue;
			}
			Logger.info("Created renderer instance.");
			break;
		}
		if (renderer == null) {
			Logger.error("No pdf renderers found, exiting.");
			JOptionPane.showMessageDialog(
				null,
				resources.getString(I18nKeys.NO_PDF_RENDERER_TEXT),
				resources.getString(I18nKeys.NO_PDF_RENDERER_TITLE),
				JOptionPane.ERROR_MESSAGE
			);
			System.exit(102);
		}
	}
	
	public static PdfEditor getEditor() {
		return editor;
	}
	
	public static PdfRenderer getRenderer() {
		return renderer;
	}
	
	public static Class<? extends PdfEditor> getEditorClass() {
		return editor.getClass();
	}
	
	public static Class<? extends PdfRenderer> getRendererClass() {
		return renderer.getClass();
	}
	
	public static void setEditorClass(Class<? extends PdfEditor> cls) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PdfEditor oldEditor = editor;
		editor = cls.getDeclaredConstructor().newInstance();
		fireEditorChanged(oldEditor);
		Preferences.userNodeForPackage(PdfJumbler.class).put("editor", cls.getCanonicalName());
	}
	
	public static void setRendererClass(Class<? extends PdfRenderer> cls) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PdfRenderer oldRenderer = renderer;
		renderer = cls.getDeclaredConstructor().newInstance();
		fireRendererChanged(oldRenderer);
		Preferences.userNodeForPackage(PdfJumbler.class).put("renderer", cls.getCanonicalName());
	}
	
	public static String getFriendlyName(Class<?> cls) {
		try {
			Method m = cls.getMethod("getFriendlyName");
			return (String)m.invoke(null);
		}
		catch (Exception e) {
			return cls.getCanonicalName();
		}
	}
	
	public static Collection<Class<? extends PdfEditor>> getAvailableEditors() {
		return Collections.unmodifiableCollection(pdfEditorClasses);
	}
	
	public static Collection<Class<? extends PdfRenderer>> getAvailableRenderers() {
		return Collections.unmodifiableCollection(pdfRendererClasses);
	}
	
	private static void fireEditorChanged(PdfEditor oldEditor) {
		for (PdfProcessorListener listener : listeners) {
			Logger.trace("Editor changed, notifying pdf processor listener: {}", listener);
			listener.pdfEditorChanged(oldEditor, editor);
		}
	}

	private static void fireRendererChanged(PdfRenderer oldRenderer) {
		for (PdfProcessorListener listener : listeners) {
			Logger.trace("Renderer changed, notifying pdf processor listener: {}", listener);
			listener.pdfRendererChanged(oldRenderer, renderer);
		}
	}
	
	public static void addProcessorListener(PdfProcessorListener listener) {
		listeners.add(listener);
	}
	
	public static void removeProcessorListener(PdfProcessorListener listener) {
		listeners.remove(listener);
	}
}