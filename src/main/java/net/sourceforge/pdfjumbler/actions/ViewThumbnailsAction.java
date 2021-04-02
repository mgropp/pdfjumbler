package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.PdfList;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;

public class ViewThumbnailsAction extends AbstractAction {
	private static final long serialVersionUID = 7357355236947950441L;
	private final PdfJumbler parent;

	public ViewThumbnailsAction(PdfJumbler parent) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.VIEW_THUMBNAILS));
		this.parent = parent;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.VIEW_THUMBNAILS));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PdfList mainList = parent.getMainPdfList();
		mainList.setShowCellText(false);
	}
}
