package net.sourceforge.pdfjumbler.pdf.itext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import net.sourceforge.pdfjumbler.pdf.Page;

/**
 * PdfJumbler interface to iText.
 * 
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public class PdfEditor implements net.sourceforge.pdfjumbler.pdf.PdfEditor {
	public static String getFriendlyName() {
		return "iText";
	}

	public static double getRequiredVersion() {
		return 0.15;
	}

	@Override
	public void close() throws IOException { }

	// TODO: Update bookmarks?
	@Override
	public void saveDocument(List<Page> pages, File file) throws IOException {
		if (pages.size() == 0) {
			throw new IOException("Empty document.");
		}

		Document document = new Document();
		PdfCopy writer;
		try {
			writer = new PdfCopy(document, new FileOutputStream(file));
		}
		catch (DocumentException e) {
			throw new IOException(e);
		}
		
		writer.setMergeFields();
		document.open();
		
		Map<File,PdfReader> readers = new HashMap<File,PdfReader>();
		try {
			for (Page page : pages) {
				PdfReader reader = readers.get(page.getFile()); 
				if (reader == null) {
					reader = new PdfReader(page.getFile().toURI().toURL());
					readers.put(page.getFile(), reader);
				}
				
				try {
					writer.addPage(
						writer.getImportedPage(
							reader,
							page.getIndex() + 1
						)
					);
				}
				catch (BadPdfFormatException e) {
					throw new IOException(e);
				}
			}
			
			// Is this handled by setMergeFields?
			/*
			for (PdfReader reader : readers.values()) {
				PRAcroForm form = reader.getAcroForm();
				if (form != null) {
					try {
						writer.copyAcroForm(reader);
					}
					catch (BadPdfFormatException e) {
						throw new IOException();
					}
				}
			}
			*/
			
			document.close();
			writer.close();
		}
		finally {
			for (PdfReader reader : readers.values()) {
				reader.close();
			}
		}
	}

}