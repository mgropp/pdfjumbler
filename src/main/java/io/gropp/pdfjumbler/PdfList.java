package io.gropp.pdfjumbler;

import io.gropp.pdfjumbler.jdragdroplist.*;
import io.gropp.pdfjumbler.pdf.Page;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.JList;
import javax.swing.TransferHandler.TransferSupport;
import org.jspecify.annotations.Nullable;

public class PdfList extends JDragDropList<Page> {
    private static final long serialVersionUID = 7475943073466784769L;

    private static final int DEFAULT_VISIBLE_ROW_COUNT = 8;

    private @Nullable String displayMessage;

    public PdfList(boolean showText) {
        super(new UndoableListModel<>());
        setDropListener(new URIDropListener());
        addMouseWheelListener(new ZoomMouseWheelListener());

        setCellRenderer(new PdfCellRenderer(PdfProcessingFactory.getInstance().getRenderer(), showText));
        this.setShowCellText(showText);
    }

    private void drawCenteredText(Graphics g, String message, boolean alignCenter) {
        String[] lines = message.split("\n");

        FontMetrics fm = g.getFontMetrics();
        int height = lines.length * fm.getHeight();
        int width = 0;
        if (!alignCenter) {
            for (String line : lines) {
                if (fm.stringWidth(line) > width) {
                    width = fm.stringWidth(line);
                }
            }
        }

        for (int i = 0; i < lines.length; i++) {
            g.drawString(
                    lines[i],
                    (getWidth() - (alignCenter ? fm.stringWidth(lines[i]) : width)) / 2,
                    (getHeight() - height) / 2 + i * fm.getHeight());
        }
    }

    @Override
    public void paint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g);

        if (getDisplayMessage() != null) {
            if (getModel().getSize() == 0) {
                g.setColor(Color.LIGHT_GRAY);
                drawCenteredText(g, getDisplayMessage(), true);
            }
        }
    }

    public int getThumbnailSize() {
        if (!(getCellRenderer() instanceof PdfCellRenderer)) {
            throw new IllegalStateException("Wrong cell renderer in use.");
        }

        return ((PdfCellRenderer) getCellRenderer()).getThumbnailWidth();
    }

    public void setThumbnailSize(int size) {
        if (!(getCellRenderer() instanceof PdfCellRenderer)) {
            throw new IllegalStateException("Wrong cell renderer in use.");
        }
        if (size < 0) {
            throw new IllegalArgumentException(Integer.toString(size));
        }

        // TODO: Aspect ratio of first?/most? pages.
        ((PdfCellRenderer) getCellRenderer()).setThumbnailWidth(size);
        ((PdfCellRenderer) getCellRenderer()).setThumbnailHeight((int) (size * Math.sqrt(2)));
        updateUI();
    }

    public boolean getShowCellText() {
        if (!(getCellRenderer() instanceof PdfCellRenderer)) {
            throw new IllegalStateException("Wrong cell renderer in use.");
        }

        return ((PdfCellRenderer) getCellRenderer()).getShowText();
    }

    public void setShowCellText(boolean value) {
        if (!(getCellRenderer() instanceof PdfCellRenderer)) {
            throw new IllegalStateException("Wrong cell renderer in use.");
        }

        ((PdfCellRenderer) getCellRenderer()).setShowText(value);

        if (value) {
            setVisibleRowCount(DEFAULT_VISIBLE_ROW_COUNT);
            setLayoutOrientation(JList.VERTICAL);
        } else {
            setVisibleRowCount(-1);
            setLayoutOrientation(JList.HORIZONTAL_WRAP);
        }
        updateUI();
    }

    public void setDisplayMessage(@Nullable String displayMessage) {
        this.displayMessage = displayMessage;
        if (getModel().getSize() == 0) {
            invalidate();
        }
    }

    public @Nullable String getDisplayMessage() {
        return displayMessage;
    }

    private final class ZoomMouseWheelListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent event) {
            if (event.isControlDown() && (getCellRenderer() instanceof PdfCellRenderer)) {
                int zoomSpeed = 20;
                setThumbnailSize(Math.max(10, getThumbnailSize() - zoomSpeed * event.getWheelRotation()));
            } else if (getParent() != null) {
                getParent().dispatchEvent(event);
            }
        }
    }

    private final class URIDropListener implements DropListener {
        @Override
        public boolean acceptDrop(Object sender, TransferSupport info) {
            return (info.getTransferable().isDataFlavorSupported(JDDLTransferData.DATA_FLAVOR))
                    || DropUtil.isURIDrop(info);
        }

        @Override
        public boolean handleDrop(Object sender, TransferSupport info) {
            @SuppressWarnings("unchecked")
            JDragDropList<Page> list = (JDragDropList<Page>) sender;
            if (DropUtil.isURIDrop(info)) {
                ArrayList<File> files = new ArrayList<>();
                int position = list.getDropLocation().getIndex();
                for (URI uri : Objects.requireNonNull(DropUtil.getURIs(info))) {
                    if (uri.getScheme().equals("file")) {
                        files.add(new File(uri.getPath()));
                    }
                }

                PdfJumbler.openFiles(PdfList.this, position, files);

                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public UndoableListModel<Page> getModel() {
        return super.getModel();
    }
}
