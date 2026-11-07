package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import io.gropp.pdfjumbler.ui.Dialogs;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;

public class ClearAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = 8258949195959150573L;

    private final Dialogs dialogs;
    private final PdfList mainList;

    public ClearAction(Dialogs dialogs, PdfList mainList) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.CLEAR_LIST), null);
        this.dialogs = dialogs;
        this.mainList = mainList;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.CLEAR_LIST));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UndoableListModel<Page> model = mainList.getModel();
        if (model.getSize() > 0
                && dialogs.confirmWarning(
                        PdfJumblerResources.getResources().getString(I18nKeys.CONFIRM_CLEAR_LIST_TEXT),
                        PdfJumblerResources.getResources().getString(I18nKeys.CONFIRM_CLEAR_LIST_TITLE))) {
            model.clear();
        }
    }
}
