package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.io.Serial;
import javax.swing.*;
import javax.swing.undo.UndoManager;

public class RotateCounterClockwiseAction extends RotateAction {
    @Serial
    private static final long serialVersionUID = 8258949195959150573L;

    public RotateCounterClockwiseAction(PdfList pdfList, UndoManager undoManager) {
        super(
                PdfJumblerResources.getResources().getString(I18nKeys.ROTATE_CCW),
                Icons.ROTATE_CCW,
                pdfList,
                undoManager,
                270);
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ROTATE_CCW));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_ROTATE_CCW));
    }
}
