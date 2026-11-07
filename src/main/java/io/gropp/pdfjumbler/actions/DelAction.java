package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.jdragdroplist.ListOperations;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;

public class DelAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = 8258949195959150573L;

    private final PdfList pdfList;

    public DelAction(PdfList pdfList) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.DELETE), Icons.EDIT_DELETE);
        this.pdfList = pdfList;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.DELETE));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_DELETE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ListOperations.removeAll(pdfList.getModel(), pdfList.getSelectedIndices());
    }
}
