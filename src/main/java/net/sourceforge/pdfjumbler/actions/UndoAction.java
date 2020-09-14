package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import org.tinylog.Logger;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

public class UndoAction extends AbstractAction {
	private static final long serialVersionUID = 4824090977507378704L;
	private final UndoManager undoManager;

	public UndoAction(UndoManager undoManager) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.UNDO));
		this.undoManager = undoManager;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.UNDO));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_UNDO));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Logger.debug("Can undo: {}", undoManager.canUndo());
		if (undoManager.canUndo()) {
			Logger.debug("Undoing: {}", undoManager.getPresentationName());
			undoManager.undo();
			Logger.debug("Next undo event: {}", undoManager.getPresentationName());
		}
	}
}
