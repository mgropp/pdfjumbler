package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.Action;
import javax.swing.undo.UndoManager;
import org.junit.jupiter.api.Test;

class UndoRedoActionsTest {
    private final UndoableListModel<Page> model = new UndoableListModel<>();
    private final UndoManager undoManager = new UndoManager();
    private final Action undo = new UndoAction(undoManager);
    private final Action redo = new RedoAction(undoManager);

    UndoRedoActionsTest() {
        model.addUndoableEditListener(undoManager);
    }

    private static void perform(Action action) {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void undoThenRedoReappliesChange() {
        model.add(0, new Page(new File("a.pdf"), 0));

        perform(undo);
        assertThat(model.getSize()).isZero();

        perform(redo);
        assertThat(model.getSize()).isEqualTo(1);
    }

    @Test
    void undoAndRedoWithEmptyHistoryDoNothing() {
        perform(undo);
        perform(redo);

        assertThat(model.getSize()).isZero();
    }

    @Test
    void newChangeDiscardsRedoHistory() {
        model.add(0, new Page(new File("a.pdf"), 0));
        perform(undo);

        model.add(0, new Page(new File("b.pdf"), 0));
        perform(redo);

        assertThat(model.getSize()).isEqualTo(1);
        assertThat(model.get(0).getFile()).isEqualTo(new File("b.pdf"));
    }
}
