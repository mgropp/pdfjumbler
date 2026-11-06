package io.gropp.pdfjumbler.jdragdroplist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.undo.UndoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UndoableListModelTest {
    private UndoableListModel<String> model;
    private UndoManager undoManager;
    private final List<String> events = new ArrayList<>();

    @BeforeEach
    void setUp() {
        model = new UndoableListModel<>();
        undoManager = new UndoManager();
        model.addUndoableEditListener(undoManager);
        model.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                events.add("added " + e.getIndex0() + "-" + e.getIndex1());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                events.add("removed " + e.getIndex0() + "-" + e.getIndex1());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                events.add("changed " + e.getIndex0() + "-" + e.getIndex1());
            }
        });
    }

    @Test
    void addAndRemoveUpdateModelAndFireEvents() {
        model.add(0, "a");
        model.add(1, "b");
        model.remove(0);

        assertThat(model.getSize()).isEqualTo(1);
        assertThat(model.getElementAt(0)).isEqualTo("b");
        assertThat(events).containsExactly("added 0-0", "added 1-1", "removed 0-0");
    }

    @Test
    void undoIsForwardedAsModelEvents() {
        model.add(0, "a");
        events.clear();

        undoManager.undo();

        assertThat(model.getSize()).isZero();
        assertThat(events).containsExactly("removed 0-0");
    }

    @Test
    void clearRemovesAllAndIsUndoable() {
        model.add(0, "a");
        model.add(1, "b");

        model.clear();
        assertThat(model.getSize()).isZero();

        undoManager.undo();
        assertThat(model.getList()).containsExactly("a", "b");
    }

    @Test
    void compoundEditGroupsChanges() {
        model.beginCompoundEdit("Open");
        model.add(0, "a");
        model.add(1, "b");
        model.endCompoundEdit();

        undoManager.undo();

        assertThat(model.getSize()).isZero();
        assertThat(undoManager.canUndo()).isFalse();
    }

    @Test
    void getListIsUnmodifiableView() {
        model.add(0, "a");
        List<String> view = model.getList();

        assertThatThrownBy(() -> view.add("b")).isInstanceOf(UnsupportedOperationException.class);

        model.add(1, "b");
        assertThat(view).containsExactly("a", "b");
    }

    @Test
    void fireContentsChangedNotifiesListeners() {
        model.add(0, "a");
        events.clear();

        model.fireContentsChanged(0, 0);

        assertThat(events).containsExactly("changed 0-0");
    }
}
