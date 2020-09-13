package net.sourceforge.pdfjumbler.pdf;

import java.io.File;
import java.util.ResourceBundle;

import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

/**
 * Represents a page from a PDF file.
 *
 * @author Martin Gropp
 */
public final class Page {
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());
	
	private File file;
	private int index;
	private int rotation = 0;
	
	public Page(File file, int index) {
		this.file = file;
		this.index = index;
	}

	/**
	 * Get the file this page originates from.
	 *
	 * @return
	 *   the page's source file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Set the file this page originates from.
	 *
	 * @param file
	 *   the page's source file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Return the page number w.r.t. the original file.
	 *
	 * @return
	 *   the original page number (0 based)
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Set the page number w.r.t. the original file.
	 *
	 * @param index
	 *   the original page number (0 based)
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Get the rotation angle.
	 *
	 * @return
	 *   the rotation angle in degrees
	 */
	public int getRotation() {
		return rotation;
	}

	/**
	 * Set the rotation angle.
	 * Supported values: 0, 90, 180, 270 (degrees)
	 *
	 * @param rotation
	 *   the rotation angle in degrees
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Page)) {
			return false;
		}
		
		return
			file.equals(((Page)o).file) &&
			(index == ((Page)o).index) &&
			(rotation == ((Page)o).rotation);
	}

	@Override
	public final int hashCode() {
		return 32 * (32 * file.hashCode() + index) + rotation;
	}
	
	@Override
	public String toString() {
		return String.format(resources.getString(I18nKeys.PDF_PAGE_TITLE), index + 1, file.getName());
	}
}
