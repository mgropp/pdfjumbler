package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

public class RedoAction extends AbstractAction {
	private static final long serialVersionUID = -4360624396939210557L;
	private final UndoManager undoManager;

	public RedoAction(UndoManager undoManager) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.REDO));
		this.undoManager = undoManager;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.REDO));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_REDO));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (undoManager.canRedo()) {
			undoManager.redo();
		}
	}
}
