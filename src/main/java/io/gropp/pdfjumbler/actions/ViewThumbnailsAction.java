package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.ConfigKeys;
import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.PdfList;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

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
		parent.getMainPdfList().setShowCellText(false);
		parent.getSecondaryPdfList().setShowCellText(false);
		Preferences.userNodeForPackage(PdfJumbler.class).putBoolean(ConfigKeys.SHOW_TEXT, false);
	}
}
