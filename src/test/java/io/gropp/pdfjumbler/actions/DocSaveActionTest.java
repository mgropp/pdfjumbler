package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.testutil.RecordingDialogs;
import io.gropp.pdfjumbler.testutil.SynchronousWorkers;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.testutil.TestPdfs;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocSaveActionTest {
    private final RecordingDialogs dialogs = new RecordingDialogs();
    private final PdfList list = TestPdfLists.pdfList(0);
    private final FileOperations fileOperations = new FileOperations(
            dialogs, SynchronousWorkers::run, new DocumentManager(file -> 2, () -> {}), TestPdfLists.processing());
    private final DocSaveAction action = new DocSaveAction(dialogs, fileOperations, list);

    @TempDir
    Path directory;

    @BeforeEach
    void addPage() throws IOException {
        File source = TestPdfs.create(directory.resolve("source.pdf").toFile(), 2);
        list.getModel().add(0, new Page(source, 1));
    }

    private void perform() {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void saveAppendsPdfExtensionWhenMissing() throws IOException {
        dialogs.fileToSave = directory.resolve("result").toFile();

        perform();

        assertThat(TestPdfs.read(directory.resolve("result.pdf").toFile())).containsExactly(TestPdfs.pageInfo(1, 0));
        assertThat(directory.resolve("result")).doesNotExist();
    }

    @Test
    void saveKeepsExtensionCaseInsensitively() {
        dialogs.fileToSave = directory.resolve("RESULT.PDF").toFile();

        perform();

        assertThat(directory.resolve("RESULT.PDF")).exists();
        assertThat(directory.resolve("RESULT.PDF.pdf")).doesNotExist();
    }

    @Test
    void cancelledChoiceSavesNothing() throws IOException {
        dialogs.fileToSave = null;

        perform();

        try (var files = Files.list(directory)) {
            assertThat(files).containsExactly(directory.resolve("source.pdf"));
        }
    }

    @Test
    void existingFileIsOverwrittenOnlyAfterConfirmation() throws IOException {
        Path target = directory.resolve("existing.pdf");
        Files.writeString(target, "old");
        dialogs.fileToSave = target.toFile();
        dialogs.confirmationAnswer = true;

        perform();

        assertThat(dialogs.confirmations).hasSize(1);
        assertThat(TestPdfs.read(target.toFile())).containsExactly(TestPdfs.pageInfo(1, 0));
    }

    @Test
    void declinedOverwriteKeepsExistingFile() throws IOException {
        Path target = directory.resolve("existing.pdf");
        Files.writeString(target, "old");
        dialogs.fileToSave = target.toFile();
        dialogs.confirmationAnswer = false;

        perform();

        assertThat(target).hasContent("old");
    }

    @Test
    void newFileIsSavedWithoutConfirmation() {
        dialogs.fileToSave = directory.resolve("fresh.pdf").toFile();

        perform();

        assertThat(dialogs.confirmations).isEmpty();
    }
}
