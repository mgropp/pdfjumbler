package net.sourceforge.pdfjumbler.pdf;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

/**
 * In der Hoffnung, dass es irgendwann einmal /eine/
 * Bibliothek gibt, die das alles kann...
 * 
 * @author Martin Gropp
 */
public final class PdfProcessingFactory {
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());
	
	private static List<Class<? extends PdfEditor>> pdfEditorClasses = new ArrayList<>();
	private static List<Class<? extends PdfRenderer>> pdfRendererClasses = new ArrayList<>();
	
	private static PdfEditor editor = null;
	private static PdfRenderer renderer = null;
	
	private static LinkedList<PdfProcessorListener> listeners = new LinkedList<PdfProcessorListener>();
	
	static {
		discoverPlugins();
		selectInitialEditor();
		selectInitialRenderer();
	}
	
	private static void discoverPlugins() {
		File pluginPath = getPluginPath();
		if (pluginPath == null) {
			return;
		}
		
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
			Plugin plugin;
			try {
				plugin = new Plugin(jarFile);
			}
			catch (IOException e) {
				// Ignore this plugin
				continue;
			}
			catch (PluginException e) {
				JOptionPane.showMessageDialog(
					null,
					resources.getString(e.getMessage()),
					resources.getString("PLUGIN_ERROR_TITLE"),
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
			System.err.print("Trying to instantiate editor: " + cls + "... ");
			try {
				editor = cls.newInstance();
			}
			catch (Exception e) {
				System.err.println("Error: " + e.getClass().getCanonicalName());
				e.printStackTrace();
				continue;
			}
			System.err.println("ok.");
			break;
		}
		if (editor == null) {
			JOptionPane.showMessageDialog(
				null,
				resources.getString("NO_PDF_EDITOR_TEXT"),
				resources.getString("NO_PDF_EDITOR_TITLE"),
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
			
			System.err.print("Trying to instantiate renderer: " + cls + "... ");
			try {
				renderer = cls.newInstance();
			}
			catch (Exception e) {
				System.err.println("Error: " + e.getClass().getCanonicalName());
				continue;
			}
			System.err.println("ok.");
			break;
		}
		if (renderer == null) {
			JOptionPane.showMessageDialog(
				null,
				resources.getString("NO_PDF_RENDERER_TEXT"),
				resources.getString("NO_PDF_RENDERER_TITLE"),
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
	
	public static void setEditorClass(Class<? extends PdfEditor> cls) throws InstantiationException, IllegalAccessException {
		PdfEditor oldEditor = editor;
		editor = cls.newInstance();
		fireEditorChanged(oldEditor);
		Preferences.userNodeForPackage(PdfJumbler.class).put("editor", cls.getCanonicalName());
	}
	
	public static void setRendererClass(Class<? extends PdfRenderer> cls) throws InstantiationException, IllegalAccessException {
		PdfRenderer oldRenderer = renderer;
		renderer = cls.newInstance();
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
			System.err.println(listener);
			listener.pdfEditorChanged(oldEditor, editor);
		}
	}

	private static void fireRendererChanged(PdfRenderer oldRenderer) {
		for (PdfProcessorListener listener : listeners) {
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