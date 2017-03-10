package net.sourceforge.pdfjumbler.pdf.jpedal;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.pdfjumbler.pdf.Page;

import org.jpedal.PdfDecoderFX;
import org.jpedal.exception.PdfException;

/**
 * PdfJumbler interface to JPedal.
 * 
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public class PdfRenderer implements net.sourceforge.pdfjumbler.pdf.PdfRenderer {
	public static String getFriendlyName() {
		return "JPedal";
	}

	public static double getRequiredVersion() {
		return 0.15;
	}
	
	private Map<File,PdfDecoderFX> docMap = new HashMap<File,PdfDecoderFX>();
	
	private PdfDecoderFX openDocument(File file) throws IOException {
		PdfDecoderFX pdfDecoder = new PdfDecoderFX();
		//PdfDecoderFX.setFontReplacements(pdfDecoder);
		try {
			pdfDecoder.openPdfFile(file.getAbsolutePath());
		}
		catch (PdfException e) {
			throw new IOException(e);
		}
		docMap.put(file, pdfDecoder);
		
		return pdfDecoder;
	}
	
	@Override
	public int getNumberOfPages(File file) throws IOException {
		PdfDecoderFX pdfDecoder = docMap.get(file);
		if (pdfDecoder == null) {
			pdfDecoder = openDocument(file);
		}

		return pdfDecoder.getPageCount();
	}
	
	@Override
	public void close() {
		Iterator<Entry<File,PdfDecoderFX>> it = docMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<File,PdfDecoderFX> entry = it.next();
			entry.getValue().closePdfFile();
			it.remove();
		}
	}

	private static BufferedImage scaleImage(BufferedImage image, int width, int height) {
		double ax = width / (double)image.getWidth();
		double ay = height / (double)image.getHeight();
		AffineTransform transform;
		if (ax < ay) {
			transform = AffineTransform.getScaleInstance(ax, ax);
			height = (int)(image.getHeight() * ax);
		} else {
			transform = AffineTransform.getScaleInstance(ay, ay);
			width = (int)(image.getWidth() * ay);
		}
		
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		Graphics2D g = scaledImage.createGraphics();		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(image, transform, null);
		g.dispose();
		
		return scaledImage;
	}
	
	@Override
	public Image renderPage(Page page, int maxWidth, int maxHeight) throws IOException {
		PdfDecoderFX pdfDecoder = docMap.get(page.getFile());
		if (pdfDecoder == null) {
			pdfDecoder = openDocument(page.getFile());
		}
		
		try {
			return scaleImage(
				pdfDecoder.getPageAsImage(page.getIndex() + 1),
				maxWidth, maxHeight
			);
		}
		catch (PdfException e) {
			throw new IOException(e);
		}
	}

}
