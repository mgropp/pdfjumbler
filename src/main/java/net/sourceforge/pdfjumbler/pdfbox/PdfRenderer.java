package net.sourceforge.pdfjumbler.pdfbox;

import net.sourceforge.pdfjumbler.pdf.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * PdfJumbler interface to PDFBox.
 *
 * @author Martin Gropp
 */
public class PdfRenderer implements net.sourceforge.pdfjumbler.pdf.PdfRenderer {
	public static String getFriendlyName() {
		return "PDFBox";
	}

	private final Map<File, PDDocument> docMap = new HashMap<>();
	private final Map<File, Map<Page, PDPage>> pageMap = new HashMap<>();

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

	private static BufferedImage rotate(BufferedImage srcImage, int degrees) {
		// TODO: this is not fast

		if (degrees == 0) {
			return srcImage;
		} else if (degrees == 90 || degrees == 180 || degrees == 270) {
			int srcWidth = srcImage.getWidth();
			int srcHeight = srcImage.getHeight();

			BufferedImage dstImage;
			if (degrees == 180) {
				dstImage = new BufferedImage(srcWidth, srcHeight, srcImage.getType());

				for (int x = 0; x < srcWidth; x++) {
					for (int y = 0; y < srcHeight; y++) {
						dstImage.setRGB(
							srcWidth - x - 1,
							srcHeight - y - 1,
							srcImage.getRGB(x, y)
						);
					}
				}
			} else {
				dstImage = new BufferedImage(srcHeight, srcWidth, srcImage.getType());

				if (degrees == 90) {
					for (int x = 0; x < srcWidth; x++) {
						for (int y = 0; y < srcHeight; y++) {
							dstImage.setRGB(
								srcHeight - y - 1,
								x,
								srcImage.getRGB(x, y)
							);
						}
					}
				} else {
					for (int x = 0; x < srcWidth; x++) {
						for (int y = 0; y < srcHeight; y++) {
							dstImage.setRGB(
								y,
								srcWidth - x - 1,
								srcImage.getRGB(x, y)
							);
						}
					}
				}
			}

			return dstImage;
		} else {
			throw new IllegalArgumentException("Unsupported rotation: " + degrees);
		}
	}

	@Override
	public Image renderPage(Page page, int maxWidth, int maxHeight) throws IOException {
		PDPage pdPage = getPDPage(page);
		PDRectangle cropBox = pdPage.getCropBox();

		double ax;
		double ay;

		int degrees = page.getRotation();
		if (degrees == 0 || degrees == 180) {
			ax = maxWidth / cropBox.getWidth();
			ay = maxHeight / cropBox.getHeight();
		} else if (degrees == 90 || degrees == 270) {
			ax = maxWidth / cropBox.getHeight();
			ay = maxHeight / cropBox.getWidth();
		} else {
			throw new IllegalArgumentException("Unsupported rotation: " + degrees);
		}

		PDFRenderer renderer = new PDFRenderer(getDocument(page.getFile()));
		BufferedImage image = renderer.renderImage(page.getIndex(), (float)Math.min(ax, ay));

		return rotate(image, degrees);
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
			pageMap.remove(entry.getKey());
			it.remove();
		}
	}
}