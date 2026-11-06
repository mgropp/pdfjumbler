package io.gropp.pdfjumbler.pdfbox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.gropp.pdfjumbler.pdf.Page;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.pdmodel.PDPage;
import org.tinylog.Logger;

/**
 * PdfJumbler interface to PDFBox.
 * 
 * @author Martin Gropp
 */
public class PdfEditor implements io.gropp.pdfjumbler.pdf.PdfEditor {
	public static String getFriendlyName() {
		return "PDFBox";
	}
	
	@Override
	public void close() {}

	@Override
	public void saveDocument(List<Page> pages, File file) throws IOException {
		Logger.info("Saving {} pages to {}", pages.size(), file);

		if (pages.size() == 0) {
			throw new IOException("Empty document.");
		}

		Map<File, PDDocument> docs = new HashMap<>();
		try (PDDocument outDoc = new PDDocument()) {
			for (Page page : pages) {
				PDDocument pageDoc = docs.get(page.getFile());
				if (pageDoc == null) {
					pageDoc = Loader.loadPDF(page.getFile());
					docs.put(page.getFile(), pageDoc);
				}

				PDPage sourcePage = pageDoc.getPage(page.getIndex());
				PDPage pdPage = new PDPage(new COSDictionary(sourcePage.getCOSObject()));
				pdPage.setRotation((sourcePage.getRotation() + page.getRotation()) % 360);

				outDoc.addPage(pdPage);
			}

			outDoc.save(file.toString());
			Logger.info("File saved successfully.");
		}
		finally {
			for (PDDocument doc : docs.values()) {
				doc.close();
			}
		}
	}

}