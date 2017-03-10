package net.sourceforge.pdfjumbler.pdf.jpod;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.intarsys.cwt.awt.environment.CwtAwtGraphicsContext;
import de.intarsys.cwt.environment.IGraphicsContext;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.platform.cwt.rendering.CSPlatformRenderer;
import de.intarsys.tools.locator.FileLocator;

import net.sourceforge.pdfjumbler.pdf.Page;

/**
 * PdfJumbler interface to JPod.
 * 
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public class PdfRenderer implements net.sourceforge.pdfjumbler.pdf.PdfRenderer {
	public static String getFriendlyName() {
		return "JPod";
	}
	
	public static double getRequiredVersion() {
		return 0.15;
	}
	
	private Map<File,PDDocument> docMap = new HashMap<File,PDDocument>();
	private Map<File,Map<Page,PDPage>> pageMap = new HashMap<File,Map<Page,PDPage>>();
	
	private PDDocument openDocument(File file) throws IOException {
		PDDocument pdDoc;
		try {
			pdDoc = PDDocument.createFromLocator(new FileLocator(file));
		}
		catch (COSLoadException e) {
			throw new IOException(e);
		}
		docMap.put(file, pdDoc);
		pageMap.put(file, new HashMap<Page,PDPage>());
		
		return pdDoc;
	}
	
	@Override
	public void close() throws IOException {
		Iterator<Entry<File,PDDocument>> it = docMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<File,PDDocument> entry = it.next();
			entry.getValue().close();
			pageMap.remove(entry.getValue());
			it.remove();
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
			pdPage = docMap.get(page.getFile()).getPageTree().getPageAt(page.getIndex());
			map.put(page, pdPage);
		}
		
		return pdPage;
	}

	@Override
	public int getNumberOfPages(File file) throws IOException {
		PDDocument pdDoc = docMap.get(file);
		if (pdDoc == null) {
			pdDoc = openDocument(file);
		}
		
		return pdDoc.getPageTree().getCount();
	}
	
	@Override
	public BufferedImage renderPage(Page page, int maxWidth, int maxHeight) throws IOException {
		return renderPage(getPDPage(page), maxWidth, maxHeight);
	}
	
	private static BufferedImage renderPage(PDPage page, int maxWidth, int maxHeight) {
		Rectangle2D rect = page.getCropBox().toNormalizedRectangle();
		double ax = maxWidth / rect.getWidth();
		double ay = maxHeight / rect.getHeight();
		
		return renderPage(page, Math.min(ax, ay));
	}

	private static BufferedImage renderPage(PDPage page, double scale) {
		Rectangle2D rect = page.getCropBox().toNormalizedRectangle();
		BufferedImage image = null;
		IGraphicsContext graphics = null;
		try {
			image = new BufferedImage(
				(int)(rect.getWidth() * scale),
				(int)(rect.getHeight() * scale),
				BufferedImage.TYPE_INT_RGB
			);
			Graphics2D g = (Graphics2D) image.getGraphics();
			graphics = new CwtAwtGraphicsContext(g);
	
			AffineTransform imgTransform = graphics.getTransform();
			imgTransform.scale(scale, -scale);
			imgTransform.translate(-rect.getMinX(), -rect.getMaxY());
			graphics.setTransform(imgTransform);
			graphics.setBackgroundColor(Color.WHITE);
			graphics.fill(rect);
			
			CSContent content = page.getContentStream();
			if (content != null) {
				CSPlatformRenderer renderer = new CSPlatformRenderer(null,
						graphics);
				renderer.process(content, page.getResources());
			}
			
			return image;
		}
		finally {
			if (graphics != null) {
				graphics.dispose();
			}
		}
	}

}
