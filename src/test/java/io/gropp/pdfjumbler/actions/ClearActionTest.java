package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.testutil.RecordingDialogs;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;
import org.junit.jupiter.api.Test;

class ClearActionTest {
    private final RecordingDialogs dialogs = new RecordingDialogs();
    private final PdfList list = TestPdfLists.pdfList(3);
    private final ClearAction action = new ClearAction(dialogs, list);

    private void perform() {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void confirmedClearEmptiesListAndCanBeUndone() {
        UndoManager undoManager = new UndoManager();
        list.getModel().addUndoableEditListener(undoManager);

        perform();

        assertThat(dialogs.confirmations).hasSize(1);
        assertThat(list.getModel().getSize()).isZero();
        undoManager.undo();
        assertThat(list.getModel().getSize()).isEqualTo(3);
    }

    @Test
    void declinedClearKeepsList() {
        dialogs.confirmationAnswer = false;

        perform();

        assertThat(list.getModel().getSize()).isEqualTo(3);
    }

    @Test
    void emptyListIsNotConfirmed() {
        list.getModel().clear();

        perform();

        assertThat(dialogs.confirmations).isEmpty();
    }
}
