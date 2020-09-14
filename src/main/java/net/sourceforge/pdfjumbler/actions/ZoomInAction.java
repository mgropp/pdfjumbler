package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.Icons;
import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.PdfList;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ZoomInAction extends AbstractAction {
	private static final long serialVersionUID = 7357355236947950441L;
	private final PdfJumbler parent;

	public ZoomInAction(PdfJumbler parent) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_IN), Icons.ZOOM_IN);
		this.parent = parent;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ZOOM_IN));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_ZOOM_IN));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PdfList mainList = parent.getMainPdfList();
		mainList.setThumbnailSize(mainList.getThumbnailSize() + 10);
	}
}
