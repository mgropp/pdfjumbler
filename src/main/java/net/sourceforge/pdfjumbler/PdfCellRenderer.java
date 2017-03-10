package net.sourceforge.pdfjumbler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.sourceforge.pdfjumbler.pdf.Page;
import net.sourceforge.pdfjumbler.pdf.PdfEditor;
import net.sourceforge.pdfjumbler.pdf.PdfProcessingFactory;
import net.sourceforge.pdfjumbler.pdf.PdfProcessorListener;
import net.sourceforge.pdfjumbler.pdf.PdfRenderer;

/**
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public class PdfCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 2395480480996785313L;

	private final Map<Page,Image> imageCache = new HashMap<Page,Image>();
	private PdfRenderer renderer;
	
	private Page page = null;
	private int padding = 6;
	
	private int textWidth = 128;
	private int thumbnailWidth = 96;
	private int thumbnailHeight = 128;
	
	public PdfCellRenderer(PdfRenderer renderer) {
		this.renderer = PdfProcessingFactory.getRenderer();
		PdfProcessingFactory.addProcessorListener(
			new PdfProcessorListener() {
				@Override
				public void pdfEditorChanged(PdfEditor oldEditor, PdfEditor newEditor) { }

				@Override
				public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer) {
					PdfCellRenderer.this.renderer = newRenderer;
					imageCache.clear();
				}
			}
		);

		sizeChanged();
	}
	
	private void sizeChanged() {
		setSize(thumbnailWidth + textWidth + 4*padding, thumbnailHeight + 2*padding);
		setPreferredSize(getSize());
		clearImageCache();
	}
	
	@Override
	public void paint(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g);

		if (page == null) {
			return;
		}

		Image cachedImage;
		
		if (imageCache.containsKey(page)) {
			cachedImage = imageCache.get(page);
		} else {
			try {
				cachedImage = renderer.renderPage(page, thumbnailWidth, thumbnailHeight);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			imageCache.put(page, cachedImage);
		}
		
		g.drawImage(cachedImage, padding, padding, null);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(padding, padding, cachedImage.getWidth(null), cachedImage.getHeight(null));
		
		g.setColor(this.getForeground());
		g.drawString(
			page.toString(),
			thumbnailWidth + 2*padding,
			padding + g.getFontMetrics().getAscent()
		);
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof Page) {
			PdfCellRenderer component = (PdfCellRenderer)super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
			assert (component == this);
			component.page = (Page)value;
			component.setLocation(0, getHeight() * index);
			return component;
		} else {
			throw new IllegalArgumentException("PdfCellRenderer can only render Page objects.");
		}
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		if (thumbnailWidth != this.thumbnailWidth) {
			this.thumbnailWidth = thumbnailWidth;
			sizeChanged();
		}
	}

	public int getThumbnailWidth() {		
		return thumbnailWidth;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		if (thumbnailHeight != this.thumbnailHeight) {
			this.thumbnailHeight = thumbnailHeight;
			sizeChanged();
		}
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}
	
	public void clearImageCache() {
		imageCache.clear();	
	}
}
