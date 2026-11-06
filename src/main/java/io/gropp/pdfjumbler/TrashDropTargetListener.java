package io.gropp.pdfjumbler;

import io.gropp.pdfjumbler.jdragdroplist.JDDLTransferData;
import io.gropp.pdfjumbler.jdragdroplist.ListOperations;
import io.gropp.pdfjumbler.pdf.Page;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

public class TrashDropTargetListener implements DropTargetListener {
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (((dtde.getDropAction() & DnDConstants.ACTION_MOVE) != 0)
                && dtde.getTransferable().isDataFlavorSupported(JDDLTransferData.DATA_FLAVOR)) {
            dtde.acceptDrag(DnDConstants.ACTION_MOVE);
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        dragEnter(dtde);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent dtde) {
        if (dtde.getTransferable().isDataFlavorSupported(JDDLTransferData.DATA_FLAVOR)) {
            JDDLTransferData<Page> data;
            try {
                data = (JDDLTransferData<Page>) dtde.getTransferable().getTransferData(JDDLTransferData.DATA_FLAVOR);
            } catch (UnsupportedFlavorException | IOException e) {
                throw new AssertionError(e);
            }

            ListOperations.removeAll(data.getSourceList().getModel(), data.getIndices());
        }
    }

    @Override
    public void dragExit(DropTargetEvent dte) {}

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}
}
