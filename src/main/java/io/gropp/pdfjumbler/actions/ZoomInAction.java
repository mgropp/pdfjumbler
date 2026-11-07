package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;

public class ZoomInAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = 7357355236947950441L;

    private final PdfList mainList;

    public ZoomInAction(PdfList mainList) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_IN), Icons.ZOOM_IN);
        this.mainList = mainList;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_IN));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_ZOOM_IN));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainList.setThumbnailSize(mainList.getThumbnailSize() + 10);
    }
}
