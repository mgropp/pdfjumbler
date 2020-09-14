package net.sourceforge.pdfjumbler;

import net.sourceforge.pdfjumbler.actions.*;

import javax.swing.*;

/**
 * Actions & workers for the main PdfJumbler class.
 *
 * @author Martin Gropp
 */
public class Actions {
	private final Action docOpenAction;
	private final Action docSaveAction;
	private final Action docOpenAction2;
	private final Action docSaveAction2;
	private final Action undoAction;
	private final Action redoAction;
	private final Action zoomOutAction;
	private final Action zoomInAction;
	private final Action clearAction;
	private final Action delAction;
	private final Action rotateCwAction;
	private final Action rotateCcwAction;
	private final Action moveUpAction;
	private final Action moveDownAction;

	public Actions(PdfJumbler parent) {
		docOpenAction = new DocOpenAction(parent, parent.getMainPdfList());
		docSaveAction = new DocSaveAction(parent, parent.getMainPdfList());
		docOpenAction2 = new DocOpenAction(parent, parent.getSecondaryPdfList(), Icons.Size16.DOCUMENT_OPEN);
		docSaveAction2 = new DocSaveAction(parent, parent.getSecondaryPdfList(), Icons.Size16.DOCUMENT_SAVE);
		undoAction = new UndoAction(parent.getUndoManager());
		redoAction = new RedoAction(parent.getUndoManager());
		zoomOutAction = new ZoomOutAction(parent);
		zoomInAction = new ZoomInAction(parent);
		clearAction = new ClearAction(parent);
		delAction = new DelAction(parent);
		rotateCwAction = new RotateClockwiseAction(parent);
		rotateCcwAction = new RotateCounterClockwiseAction(parent);
		moveUpAction = new MoveUpAction();
		moveDownAction = new MoveDownAction();

	}

	public Action getUndoAction() {
		return undoAction;
	}

	public Action getRedoAction() {
		return redoAction;
	}

	public Action getRotateClockwiseAction() {
		return rotateCwAction;
	}

	public Action getRotateCounterClockwiseAction() {
		return rotateCcwAction;
	}

	public Action getClearAction() {
		return clearAction;
	}

	public Action getDeleteAction() {
		return delAction;
	}

	public Action getDocOpenAction() {
		return docOpenAction;
	}

	public Action getDocSaveAction() {
		return docSaveAction;
	}

	public Action getDocOpenAction2() {
		return docOpenAction2;
	}

	public Action getDocSaveAction2() {
		return docSaveAction2;
	}

	public Action getZoomOutAction() {
		return zoomOutAction;
	}

	public Action getZoomInAction() {
		return zoomInAction;
	}

	public Action getMoveUpAction() {
		return moveUpAction;
	}

	public Action getMoveDownAction() {
		return moveDownAction;
	}
}
