package net.sourceforge.pdfjumbler.pdf;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public interface PdfEditor {
	/**
	 * Save pages to a PDF file.
	 * 
	 * @param pages
	 * @param file
	 * @throws IOException
	 */
	public void saveDocument(List<Page> pages, File file) throws IOException;
	
	/**
	 * Close the editor, for example because a different editor is selected. 
	 */
	public void close() throws IOException;
}
