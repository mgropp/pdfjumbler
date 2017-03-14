package net.sourceforge.pdfjumbler.pdf.pdfbox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pdfjumbler.pdf.Page;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * PdfJumbler interface to PDFBox.
 * 
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public class PdfEditor implements net.sourceforge.pdfjumbler.pdf.PdfEditor {
	public static String getFriendlyName() {
		return "PDFBox";
	}
	
	@Override
	public void close() throws IOException { }

	@Override
	public void saveDocument(List<Page> pages, File file) throws IOException {
		if (pages.size() == 0) {
			throw new IOException("Empty document.");
		}
		
		PDDocument outDoc = new PDDocument();
		
		Map<File,PDDocument> docs = new HashMap<File,PDDocument>();
		try {
			for (Page page : pages) {
				PDDocument pageDoc = docs.get(page.getFile());
				if (pageDoc == null) {
					pageDoc = PDDocument.load(page.getFile());
					docs.put(page.getFile(), pageDoc);
				}
				
				outDoc.addPage((PDPage)pageDoc.getPrintable(page.getIndex()));
			}
			
			try {
				outDoc.save(file.toString());
			}
			catch (COSVisitorException e) {
				throw new IOException(e);
			}
		}
		finally {
			outDoc.close();
			for (PDDocument doc : docs.values()) {
				doc.close();
			}
		}
	}

}