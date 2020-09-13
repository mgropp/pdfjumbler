package net.sourceforge.pdfjumbler;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * A special UndoManager that adds edits only if the
 * last edit is not the same as the new one.
 * This is needed for move operations between lists
 * (implemented as remove + add compound edits) and
 * is simpler than using insignificant edits.
 *
 * @author Martin Gropp
 */
class UniqueUndoManager extends UndoManager {
	private static final long serialVersionUID = -2740016241678747836L;

	@Override
	public boolean addEdit(UndoableEdit edit) {
		if ((lastEdit() != null) && lastEdit().equals(edit)) {
			return false;
		} else {
			return super.addEdit(edit);
		}
	}
}
