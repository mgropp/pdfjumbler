package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.ConfigKeys;
import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class ViewListAction extends AbstractAction {
    private static final long serialVersionUID = 7357355236947950441L;
    private final PdfJumbler parent;

    public ViewListAction(PdfJumbler parent) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.VIEW_LIST));
        this.parent = parent;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.VIEW_LIST));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parent.getMainPdfList().setShowCellText(true);
        parent.getSecondaryPdfList().setShowCellText(true);
        Preferences.userNodeForPackage(PdfJumbler.class).putBoolean(ConfigKeys.SHOW_TEXT, true);
    }
}
