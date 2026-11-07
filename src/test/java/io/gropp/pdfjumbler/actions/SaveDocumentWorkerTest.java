package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import io.gropp.pdfjumbler.pdf.NoPdfProcessorException;
import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import io.gropp.pdfjumbler.testutil.InMemoryPreferences;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.testutil.TestPdfs;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SaveDocumentWorkerTest {
    private final DocumentManager documentManager = new DocumentManager(file -> 3, () -> {});
    private final UndoableListModel<Page> model = new UndoableListModel<>();

    @TempDir
    Path directory;

    public static class FailingEditor implements PdfEditor {
        @Override
        public void saveDocument(List<Page> pages, File file) throws IOException {
            throw new IOException("disk full");
        }

        @Override
        public void close() {}
    }

    public static class StubRenderer implements PdfRenderer {
        @Override
        public Image renderPage(Page page, int maxWidth, int maxHeight) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getNumberOfPages(File file) {
            return 0;
        }

        @Override
        public void close() {}
    }

    private static PdfProcessingFactory failingProcessing() throws NoPdfProcessorException {
        return new PdfProcessingFactory(
                List.of(FailingEditor.class), List.of(StubRenderer.class), new InMemoryPreferences(), null, null);
    }

    @Test
    void savesPagesInListOrderWithRotation() throws Exception {
        File source = TestPdfs.create(directory.resolve("source.pdf").toFile(), 3);
        Page first = new Page(source, 0);
        Page last = new Page(source, 2);
        last.setRotation(90);
        model.add(0, last);
        model.add(1, first);
        File target = directory.resolve("out.pdf").toFile();

        new SaveDocumentWorker(new DocumentManager(file -> 3, () -> {}), TestPdfLists.processing(), model, target)
                .doInBackground();

        assertThat(TestPdfs.read(target)).containsExactly(TestPdfs.pageInfo(2, 90), TestPdfs.pageInfo(0, 0));
    }

    @Test
    void editorFailureIsPropagatedAndLeavesNoTargetFile() throws Exception {
        model.add(0, new Page(new File("a.pdf"), 0));
        File target = directory.resolve("out.pdf").toFile();
        SaveDocumentWorker worker = new SaveDocumentWorker(documentManager, failingProcessing(), model, target);

        worker.execute();

        assertThatThrownBy(worker::get).isInstanceOf(ExecutionException.class).hasRootCauseMessage("disk full");
        assertThat(target).doesNotExist();
    }

    @Test
    void savingEmptyListFails() throws Exception {
        File target = directory.resolve("out.pdf").toFile();
        SaveDocumentWorker worker = new SaveDocumentWorker(documentManager, TestPdfLists.processing(), model, target);

        assertThatThrownBy(worker::doInBackground).isInstanceOf(IOException.class);
        assertThat(target).doesNotExist();
    }
}
