package net.sourceforge.pdfjumbler.pdf;

import java.io.File;
import java.util.ResourceBundle;

import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

/**
 * @author Martin Gropp
 */
public final class Page {
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());
	
	private File file;
	private int index;
	
	public Page(File file, int index) {
		this.file = file;
		this.index = index;
	}

	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public final boolean equals(Object o) {
		if ((o == null) || !(o instanceof Page)) {
			return false;
		}
		
		return
			file.equals(((Page)o).file) &&
			(index == ((Page)o).index);
	}

	@Override
	public final int hashCode() {
		return 32 * file.hashCode() + index;
	}
	
	@Override
	public String toString() {
		return String.format(resources.getString("PDF_PAGE_TITLE"), index + 1, file.getName());
	}
}
