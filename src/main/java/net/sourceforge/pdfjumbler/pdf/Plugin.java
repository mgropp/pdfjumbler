package net.sourceforge.pdfjumbler.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

public class Plugin {
	public static final String REQUIRED_VERSION_KEY = "pdfjumbler-required-version";
	public static final String PDF_EDITOR_KEY = "pdfjumbler-plugin-editor";
	public static final String PDF_RENDERER_KEY = "pdfjumbler-plugin-renderer";
	
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());
	
	private final ClassLoader classLoader;
	private final List<Class<? extends PdfEditor>> pdfEditorClasses;
	private final List<Class<? extends PdfRenderer>> pdfRendererClasses;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Plugin(File jarFile) throws IOException, PluginException {
		Properties properties = new Properties();
		try (JarFile jar = new JarFile(jarFile)) {
			ZipEntry entry = jar.getEntry("pdfjumbler-plugin.properties");
			if (entry == null) {
				throw new IOException("Missing pdfjumbler-plugin.properties entry!");
			}
			
			try (InputStream stream = jar.getInputStream(entry)) {
				properties.load(stream);
			}
		}
		
		String pdfEditorClassNames = properties.getProperty(PDF_EDITOR_KEY);
		String pdfRendererClassNames = properties.getProperty(PDF_RENDERER_KEY);
		if (pdfEditorClassNames == null && pdfRendererClassNames == null) {
			throw new IOException("No plugins found in jar!"); 
		}
		
		String requiredVersion = properties.getProperty(REQUIRED_VERSION_KEY);
		if (requiredVersion == null) {
			throw new PluginException("Missing version declaration in plugin!");
		}
		if (Double.parseDouble(requiredVersion) > PdfJumbler.VERSION) {
			throw new PluginException(String.format(
				resources.getString("PLUGIN_ERROR_VERSION_INCOMPATIBLE"),
				requiredVersion
			));
		}
		
		try {
			classLoader = new URLClassLoader(
				new URL[]{ jarFile.toURI().toURL() },
				Plugin.class.getClassLoader()
			);
		}
		catch (MalformedURLException e) {
			// This should not happen...
			throw new AssertionError(e);
		}
		
		if (pdfEditorClassNames != null) {
			String[] classNames = pdfEditorClassNames.split(",");
			pdfEditorClasses = new ArrayList<>(classNames.length);
			for (String className : classNames) {
				try {
					pdfEditorClasses.add(
						(Class)classLoader.loadClass(
							className
						)
					);
				}
				catch (Exception e) {
					throw new PluginException(
						"Failed to load editor class from plugin!",
						e
					);
				}
			}
		} else {
			pdfEditorClasses = null;
		}
		
		if (pdfRendererClassNames != null) {
			String[] classNames = pdfRendererClassNames.split(",");
			pdfRendererClasses = new ArrayList<>(classNames.length);
			for (String className : classNames) {
				try {
					pdfRendererClasses.add(
						(Class)classLoader.loadClass(
							className
						)
					);
				}
				catch (Exception e) {
					throw new PluginException(
						"Failed to load renderer class from plugin!",
						e
					);
				}
			}
		} else {
			pdfRendererClasses = null;
		}
	}
	
	public boolean hasPdfEditors() {
		return (pdfEditorClasses != null && !pdfEditorClasses.isEmpty());
	}
	
	public boolean hasPdfRenderers() {
		return (pdfRendererClasses != null && !pdfRendererClasses.isEmpty());
	}
	
	public List<Class<? extends PdfEditor>> getPdfEditorClasses() {
		return Collections.unmodifiableList(pdfEditorClasses);
	}
	
	public List<Class<? extends PdfRenderer>> getPdfRendererClasses() {
		return Collections.unmodifiableList(pdfRendererClasses);
	}
}
