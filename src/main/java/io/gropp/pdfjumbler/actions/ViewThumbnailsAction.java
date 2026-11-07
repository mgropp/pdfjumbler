package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.config.UserPreferences;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class ViewThumbnailsAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = 7357355236947950441L;

    private final PdfList mainList;
    private final PdfList secondaryList;
    private final UserPreferences userPreferences;

    public ViewThumbnailsAction(PdfList mainList, PdfList secondaryList, UserPreferences userPreferences) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.VIEW_THUMBNAILS));
        this.mainList = mainList;
        this.secondaryList = secondaryList;
        this.userPreferences = userPreferences;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.VIEW_THUMBNAILS));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainList.setShowCellText(false);
        secondaryList.setShowCellText(false);
        userPreferences.setShowText(false);
    }
}
