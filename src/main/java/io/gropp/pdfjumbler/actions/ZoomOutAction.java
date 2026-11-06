package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.Icons;
import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.PdfList;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class ZoomOutAction extends AbstractAction {
    private static final long serialVersionUID = -8473933777713981409L;
    private final PdfJumbler parent;

    public ZoomOutAction(PdfJumbler parent) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_OUT), Icons.ZOOM_OUT);
        this.parent = parent;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_OUT));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_ZOOM_OUT));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PdfList mainList = parent.getMainPdfList();
        mainList.setThumbnailSize(mainList.getThumbnailSize() - 10);
    }
}
