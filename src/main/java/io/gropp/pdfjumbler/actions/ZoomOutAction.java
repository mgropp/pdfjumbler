package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;

public class ZoomOutAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = -8473933777713981409L;

    private final PdfList mainList;

    public ZoomOutAction(PdfList mainList) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_OUT), Icons.ZOOM_OUT);
        this.mainList = mainList;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_OUT));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_ZOOM_OUT));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainList.setThumbnailSize(mainList.getThumbnailSize() - 10);
    }
}
