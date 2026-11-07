package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.edit.RotatePagesEdit;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.undo.UndoManager;

public abstract class RotateAction extends AbstractAction {
    private final PdfList pdfList;
    private final UndoManager undoManager;
    private final int degrees;

    public RotateAction(String name, ImageIcon icon, PdfList pdfList, UndoManager undoManager, int degrees) {
        super(name, icon);
        this.pdfList = pdfList;
        this.undoManager = undoManager;
        this.degrees = degrees;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int[] selectedIndices = pdfList.getSelectedIndices();
        if (selectedIndices.length > 0) {
            undoManager.addEdit(RotatePagesEdit.apply(pdfList.getModel(), selectedIndices, degrees));
        }
    }
}
