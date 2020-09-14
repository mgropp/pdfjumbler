package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.Icons;
import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.PdfList;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
