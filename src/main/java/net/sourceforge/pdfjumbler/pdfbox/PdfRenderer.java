package net.sourceforge.pdfjumbler.pdfbox;

import net.sourceforge.pdfjumbler.pdf.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PdfRenderer implements net.sourceforge.pdfjumbler.pdf.PdfRenderer {
	public static String getFriendlyName() {
		return "PDFBox";
	}

	private Map<File, PDDocument> docMap = new HashMap<>();
	private Map<File, Map<Page, PDPage>> pageMap = new HashMap<>();

	private PDDocument openDocument(File file) throws IOException {
		PDDocument pdDoc = PDDocument.load(file);
		if (pdDoc == null) {
			throw new IOException("Failed to load PDF document.");
		}

		docMap.put(file, pdDoc);
		pageMap.put(file, new HashMap<>());

		return pdDoc;
	}

	private PDDocument getDocument(File file) throws IOException {
		PDDocument pdDoc = docMap.get(file);
		if (pdDoc != null) {
			return pdDoc;
		} else {
			return openDocument(file);
		}
	}

	private PDPage getPDPage(Page page) throws IOException {
		PDDocument pdDoc = docMap.get(page.getFile());
		if (pdDoc == null) {
			pdDoc = openDocument(page.getFile());
		}

		Map<Page,PDPage> map = pageMap.get(page.getFile());
		assert (map != null);

		PDPage pdPage;
		if (map.containsKey(page)) {
			pdPage = map.get(page);
		} else {
			pdPage = pdDoc.getPage(page.getIndex());
			map.put(page, pdPage);
		}

		return pdPage;
	}

	@Override
	public Image renderPage(Page page, int maxWidth, int maxHeight) throws IOException {
		PDPage pdPage = getPDPage(page);
		PDRectangle cropBox = pdPage.getCropBox();

		double ax = maxWidth / cropBox.getWidth();
		double ay = maxHeight / cropBox.getHeight();

		PDFRenderer renderer = new PDFRenderer(getDocument(page.getFile()));
		return renderer.renderImage(page.getIndex(), (float)Math.min(ax, ay));
	}

	@Override
	public int getNumberOfPages(File file) throws IOException {
		return getDocument(file).getNumberOfPages();
	}

	@Override
	public void close() throws IOException {
		Iterator<Map.Entry<File, PDDocument>> it = docMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<File, PDDocument> entry = it.next();
			entry.getValue().close();
			pageMap.remove(entry.getValue());
			it.remove();
		}
	}
}