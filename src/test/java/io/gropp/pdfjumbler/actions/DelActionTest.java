package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.undo.UndoManager;
import org.junit.jupiter.api.Test;

class DelActionTest {
    private final PdfList list = TestPdfLists.pdfList(5);
    private final UndoManager undoManager = new UndoManager();

    private List<Integer> remainingIndices() {
        return list.getModel().getList().stream().map(Page::getIndex).toList();
    }

    private void delete() {
        DelAction action = new DelAction(list);
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void removesOnlySelectedPages() {
        list.setSelectedIndices(new int[] {1, 3});

        delete();

        assertThat(remainingIndices()).containsExactly(0, 2, 4);
    }

    @Test
    void undoRestoresPagesInOriginalOrder() {
        list.getModel().addUndoableEditListener(undoManager);
        list.setSelectedIndices(new int[] {0, 2, 4});

        delete();
        undoManager.undo();

        assertThat(remainingIndices()).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void withoutSelectionNothingIsRemoved() {
        list.clearSelection();

        delete();

        assertThat(list.getModel().getSize()).isEqualTo(5);
    }
}
