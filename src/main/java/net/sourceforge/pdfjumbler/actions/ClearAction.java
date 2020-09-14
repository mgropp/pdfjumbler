package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableListModel;
import net.sourceforge.pdfjumbler.pdf.Page;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearAction extends AbstractAction {
	private static final long serialVersionUID = 8258949195959150573L;
	private final PdfJumbler parent;

	public ClearAction(PdfJumbler parent) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.CLEAR_LIST), null);
		this.parent = parent;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.CLEAR_LIST));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UndoableListModel<Page> model = parent.getMainPdfList().getModel();
		if (
			(model.getSize() > 0) &&
			(
				JOptionPane.showConfirmDialog(
					parent,
					PdfJumblerResources.getResources().getString(I18nKeys.CONFIRM_CLEAR_LIST_TEXT),
					PdfJumblerResources.getResources().getString(I18nKeys.CONFIRM_CLEAR_LIST_TITLE),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE
				) == JOptionPane.OK_OPTION
			)
		) {
			model.clear();
		}
	}
}
