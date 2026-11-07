package io.gropp.pdfjumbler.edit;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import org.junit.jupiter.api.Test;

class UniqueUndoManagerTest {
    private final UniqueUndoManager manager = new UniqueUndoManager();

    @Test
    void addsDistinctEdits() {
        assertThat(manager.addEdit(new AbstractUndoableEdit())).isTrue();
        assertThat(manager.addEdit(new AbstractUndoableEdit())).isTrue();
    }

    @Test
    void rejectsEditEqualToLastEdit() {
        UndoableEdit edit = new AbstractUndoableEdit();
        manager.addEdit(edit);

        assertThat(manager.addEdit(edit)).isFalse();
    }

    @Test
    void undoesRepeatedEditOnlyOnce() {
        UndoableEdit edit = new AbstractUndoableEdit();
        manager.addEdit(edit);
        manager.addEdit(edit);

        manager.undo();

        assertThat(manager.canUndo()).isFalse();
    }
}
