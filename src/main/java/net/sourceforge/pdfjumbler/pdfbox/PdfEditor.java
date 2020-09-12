package net.sourceforge.pdfjumbler.pdfbox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pdfjumbler.pdf.Page;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.tinylog.Logger;

/**
 * PdfJumbler interface to PDFBox.
 * 
 * @author Martin Gropp
 */
public class PdfEditor implements net.sourceforge.pdfjumbler.pdf.PdfEditor {
	public static String getFriendlyName() {
		return "PDFBox";
	}
	
	@Override
	public void close() throws IOException { }

	@Override
	public void saveDocument(List<Page> pages, File file) throws IOException {
		Logger.info("Saving {} pages to {}", pages.size(), file);

		if (pages.size() == 0) {
			throw new IOException("Empty document.");
		}
		
		PDDocument outDoc = new PDDocument();

		Map<File,PDDocument> docs = new HashMap<>();
		try {
			for (Page page : pages) {
				PDDocument pageDoc = docs.get(page.getFile());
				if (pageDoc == null) {
					pageDoc = PDDocument.load(page.getFile());
					docs.put(page.getFile(), pageDoc);
				}

				outDoc.addPage(pageDoc.getPage(page.getIndex()));

			}

			outDoc.save(file.toString());
			Logger.info("File saved successfully.");
		}
		finally {
			outDoc.close();
			for (PDDocument doc : docs.values()) {
				doc.close();
			}
		}
	}

}