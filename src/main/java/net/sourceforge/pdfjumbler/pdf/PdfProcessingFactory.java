package net.sourceforge.pdfjumbler.pdf;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

/**
 * In der Hoffnung, dass es irgendwann einmal /eine/
 * Bibliothek gibt, die das alles kann...
 * 
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public final class PdfProcessingFactory {
	private static final String[] EDITOR_CLASSES = {
		System.getProperty("pdfjumbler.editor", null),
		Preferences.userNodeForPackage(PdfJumbler.class).get("editor", null),
		"net.sourceforge.pdfjumbler.pdf.itext.PdfEditor",
		"net.sourceforge.pdfjumbler.pdf.pdfbox.PdfEditor"
	};
	private static final String[] RENDERER_CLASSES = {
		System.getProperty("pdfjumbler.renderer", null),
		Preferences.userNodeForPackage(PdfJumbler.class).get("renderer", null),
		"net.sourceforge.pdfjumbler.pdf.jpedal.PdfRenderer",
		"net.sourceforge.pdfjumbler.pdf.jpod.PdfRenderer"
	};
		
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());

	private static ClassLoader pluginClassLoader;
	
	private static PdfEditor editor = null;
	private static PdfRenderer renderer = null;
	
	private static LinkedList<PdfProcessorListener> listeners = new LinkedList<PdfProcessorListener>();
	
	private static File getMainJarPath() {
		try {
			URL url = PdfProcessingFactory.class.getResource(PdfProcessingFactory.class.getSimpleName() + ".class");
			if (url.getProtocol().equals("jar")) {
				String path = url.getPath();
				path = path.substring(0, path.indexOf('!'));
				URL jarURL = new URL(path);
				if (jarURL.getProtocol().equals("file")) {
					return new File(jarURL.toURI()).getParentFile();
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
	
	/**
	 * If the program is run from a local jar, plugins can be loaded
	 * from other jars in the same directory.
	 * This method creates a suitable classloader, or returns the
	 * factory's class loader if something goes wrong. 
	 * 
	 * @return
	 *   A classloader for the plugins.
	 */
	private static ClassLoader getPluginClassloader() {
		File path = getMainJarPath();
		if (path == null) {
			return PdfProcessingFactory.class.getClassLoader();
		} else {
			File[] jarFiles = path.listFiles(
				new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".jar");
					}
				}
			);
			URL[] jarUrls = new URL[jarFiles.length];
			for (int i = 0; i < jarFiles.length; i++) {
				try {
					jarUrls[i] = jarFiles[i].toURI().toURL();
				}
				catch (MalformedURLException e) {
					throw new AssertionError(e);
				}
			}
			
			return new URLClassLoader(jarUrls, PdfProcessingFactory.class.getClassLoader());
		}
	}

	static {
		pluginClassLoader = getPluginClassloader();
		for (String className : EDITOR_CLASSES) {
			if (className == null) {
				continue;
			}
			System.err.print("Trying to load editor class: " + className + "... ");
			try {
				editor = (PdfEditor)pluginClassLoader.loadClass(className).newInstance();
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
		
		
		for (String className : RENDERER_CLASSES) {
			if (className == null) {
				continue;
			}
			System.err.print("Trying to load renderer class: " + className + "... ");
			try {
				renderer = (PdfRenderer)pluginClassLoader.loadClass(className).newInstance();
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
	
	private static boolean checkPluginVersion(Class<?> cls) {
		try {
			Method m = cls.getMethod("getRequiredVersion");
			if ((Double)m.invoke(null) <= PdfJumbler.VERSION) {
				return true;
			} else {
				System.err.println("Warning: Incompatible plugin detected: " + cls.getCanonicalName());
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Class<? extends PdfEditor>> getAvailableEditors() {
		Set<Class<? extends PdfEditor>> classes = new HashSet<Class<? extends PdfEditor>>(EDITOR_CLASSES.length);
		for (String className : EDITOR_CLASSES) {
			if (className != null) {		
				try {
					Class<?> cls = pluginClassLoader.loadClass(className);
					if (PdfEditor.class.isAssignableFrom(cls)) {
						if (checkPluginVersion(cls)) {
							classes.add((Class<? extends PdfEditor>)cls);
						}
					}
				}
				catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
		
		return classes;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Class<? extends PdfRenderer>> getAvailableRenderers() {
		Set<Class<? extends PdfRenderer>> classes = new HashSet<Class<? extends PdfRenderer>>(RENDERER_CLASSES.length);
		for (String className : RENDERER_CLASSES) {
			if (className != null) {		
				try {
					Class<?> cls = pluginClassLoader.loadClass(className);
					if (PdfRenderer.class.isAssignableFrom(cls)) {
						if (checkPluginVersion(cls)) {
							classes.add((Class<? extends PdfRenderer>)cls);
						}
					}
				}
				catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
		
		return classes;
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
