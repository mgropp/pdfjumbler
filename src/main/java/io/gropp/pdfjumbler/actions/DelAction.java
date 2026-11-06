package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.Icons;
import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.jdragdroplist.JDragDropList;
import io.gropp.pdfjumbler.jdragdroplist.ListOperations;
import io.gropp.pdfjumbler.pdf.Page;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class DelAction extends AbstractAction {
    private static final long serialVersionUID = 8258949195959150573L;
    private final PdfJumbler parent;

    public DelAction(PdfJumbler parent) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.DELETE), Icons.EDIT_DELETE);
        this.parent = parent;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.DELETE));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_DELETE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        JDragDropList<Page> list;
        if (e.getSource() instanceof JDragDropList) {
            list = (JDragDropList<Page>) e.getSource();
        } else {
            list = parent.getMainPdfList();
        }
        ListOperations.removeAll(list.getModel(), list.getSelectedIndices());
    }
}
