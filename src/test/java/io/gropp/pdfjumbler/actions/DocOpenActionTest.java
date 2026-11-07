package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.testutil.RecordingDialogs;
import io.gropp.pdfjumbler.testutil.SynchronousWorkers;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

class DocOpenActionTest {
    private final RecordingDialogs dialogs = new RecordingDialogs();
    private final PdfList list = TestPdfLists.pdfList(0);
    private final FileOperations fileOperations = new FileOperations(
            dialogs, SynchronousWorkers::run, new DocumentManager(file -> 3, () -> {}), TestPdfLists.processing());
    private final DocOpenAction action = new DocOpenAction(dialogs, fileOperations, list);

    private void perform() {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void chosenFilesAreAppendedToList() {
        dialogs.filesToOpen = List.of(new File("a.pdf"), new File("b.pdf"));

        perform();

        assertThat(list.getModel().getSize()).isEqualTo(6);
    }

    @Test
    void cancelledChoiceLeavesListUntouched() {
        dialogs.filesToOpen = null;

        perform();

        assertThat(list.getModel().getSize()).isZero();
    }
}
