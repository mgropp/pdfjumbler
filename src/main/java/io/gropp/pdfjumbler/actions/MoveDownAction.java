package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.jdragdroplist.JDragDropList;
import io.gropp.pdfjumbler.jdragdroplist.ListOperations;
import io.gropp.pdfjumbler.pdf.Page;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class MoveDownAction extends AbstractAction {
    private static final long serialVersionUID = -4468213711264218766L;

    public MoveDownAction() {
        super(PdfJumblerResources.getResources().getString(I18nKeys.MOVE_DOWN));
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.MOVE_DOWN));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_MOVE_DOWN));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(e.getSource() instanceof JDragDropList<?>)) {
            return;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        JDragDropList<Page> list = (JDragDropList) e.getSource();
        int index = list.getSelectedIndex();
        if (ListOperations.move(list.getModel(), index, index + 1)) {
            list.setSelectedIndex(index + 1);
        }
    }
}
