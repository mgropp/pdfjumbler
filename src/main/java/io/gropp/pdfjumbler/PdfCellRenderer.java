package io.gropp.pdfjumbler;

import com.formdev.flatlaf.util.UIScale;
import io.gropp.pdfjumbler.pdf.Page;
import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdf.PdfProcessorListener;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.jspecify.annotations.Nullable;

public class PdfCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 2395480480996785313L;
    private static final Executor THUMBNAIL_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "thumbnail-renderer");
        thread.setDaemon(true);
        return thread;
    });

    private static final int THUMBNAIL_CACHE_CAPACITY = 256;

    private volatile PdfRenderer renderer;
    private final ThumbnailCache thumbnailCache = new ThumbnailCache(
            THUMBNAIL_CACHE_CAPACITY,
            (page, maxWidth, maxHeight) -> renderer.renderPage(page, maxWidth, maxHeight),
            THUMBNAIL_EXECUTOR,
            SwingUtilities::invokeLater,
            this::repaintOwner);
    private @Nullable JList<?> owner;

    private @Nullable Page page;
    private final int padding = 6;
    private final int textWidth = 128;

    private int thumbnailWidth = 96;
    private int thumbnailHeight = 128;

    private boolean showText;

    public PdfCellRenderer(PdfRenderer renderer) {
        this(renderer, true);
    }

    public PdfCellRenderer(PdfRenderer renderer, boolean showText) {
        this.showText = showText;
        this.renderer = renderer;
        PdfProcessingFactory.getInstance().addProcessorListener(new PdfProcessorListener() {
            @Override
            public void pdfEditorChanged(PdfEditor oldEditor, PdfEditor newEditor) {}

            @Override
            public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer) {
                PdfCellRenderer.this.renderer = newRenderer;
                thumbnailCache.clear();
            }
        });

        sizeChanged();
    }

    private void sizeChanged() {
        int padding = UIScale.scale(this.padding);
        int width = UIScale.scale(thumbnailWidth) + 2 * padding;
        int height = UIScale.scale(thumbnailHeight) + 2 * padding;
        if (showText) {
            width += UIScale.scale(textWidth) + 2 * padding;
        }

        setSize(width, height);
        setPreferredSize(getSize());
        clearImageCache();
    }

    @Override
    public void paint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g);

        if (page == null) {
            return;
        }

        int padding = UIScale.scale(this.padding);
        int scaledThumbnailWidth = UIScale.scale(thumbnailWidth);
        int scaledThumbnailHeight = UIScale.scale(thumbnailHeight);
        g.setColor(Color.LIGHT_GRAY);
        Optional<Image> thumbnail = thumbnailCache.get(page, scaledThumbnailWidth, scaledThumbnailHeight);
        if (thumbnail.isPresent()) {
            Image image = thumbnail.get();
            g.drawImage(image, padding, padding, null);
            g.drawRect(padding, padding, image.getWidth(null), image.getHeight(null));
        } else {
            g.drawRect(padding, padding, scaledThumbnailWidth, scaledThumbnailHeight);
        }

        if (showText) {
            g.setColor(this.getForeground());
            g.drawString(
                    page.toString(),
                    scaledThumbnailWidth + 2 * padding,
                    padding + g.getFontMetrics().getAscent());
        }
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
        owner = list;
        page = (Page) value;
        setLocation(0, getHeight() * index);
        return this;
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
        thumbnailCache.clear();
    }

    private void repaintOwner() {
        JList<?> list = owner;
        if (list != null) {
            list.repaint();
        }
    }

    public boolean getShowText() {
        return showText;
    }

    /**
     * Display the page's text representation next to the thumbnail iff true.
     */
    public void setShowText(boolean value) {
        this.showText = value;
        sizeChanged();
    }
}
