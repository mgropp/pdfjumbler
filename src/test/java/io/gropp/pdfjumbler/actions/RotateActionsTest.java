package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.Action;
import javax.swing.undo.UndoManager;
import org.junit.jupiter.api.Test;

class RotateActionsTest {
    private final PdfList list = TestPdfLists.pdfList(4);
    private final UndoManager undoManager = new UndoManager();

    private static void perform(Action action) {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    private List<Integer> rotations() {
        return list.getModel().getList().stream().map(Page::getRotation).toList();
    }

    @Test
    void clockwiseRotatesOnlySelectedPages() {
        list.setSelectedIndices(new int[] {1, 3});

        perform(new RotateClockwiseAction(list, undoManager));

        assertThat(rotations()).containsExactly(0, 90, 0, 90);
    }

    @Test
    void counterClockwiseRotatesOnlySelectedPages() {
        list.setSelectedIndices(new int[] {0, 2});

        perform(new RotateCounterClockwiseAction(list, undoManager));

        assertThat(rotations()).containsExactly(270, 0, 270, 0);
    }

    @Test
    void fourClockwiseRotationsRestoreOriginalRotation() {
        list.setSelectedIndices(new int[] {1});
        Action rotate = new RotateClockwiseAction(list, undoManager);

        for (int i = 0; i < 4; i++) {
            perform(rotate);
        }

        assertThat(rotations()).containsOnly(0);
    }

    @Test
    void rotationIsOneUndoableStep() {
        list.setSelectedIndices(new int[] {0, 1, 2});
        perform(new RotateClockwiseAction(list, undoManager));

        undoManager.undo();

        assertThat(rotations()).containsOnly(0);
        assertThat(undoManager.canUndo()).isFalse();
        undoManager.redo();
        assertThat(rotations()).containsExactly(90, 90, 90, 0);
    }

    @Test
    void withoutSelectionNoPageChanges() {
        list.clearSelection();

        perform(new RotateClockwiseAction(list, undoManager));

        assertThat(rotations()).containsOnly(0);
        assertThat(undoManager.canUndo()).isFalse();
    }
}
