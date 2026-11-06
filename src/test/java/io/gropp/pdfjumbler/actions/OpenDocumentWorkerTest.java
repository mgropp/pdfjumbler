package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.DocumentManager;
import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import io.gropp.pdfjumbler.pdf.Page;
import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;
import javax.swing.undo.UndoManager;
import org.junit.jupiter.api.Test;

class OpenDocumentWorkerTest {
    private final UndoableListModel<Page> model = new UndoableListModel<>();
    private final UndoManager undoManager = new UndoManager();
    private final DocumentManager documentManager = new DocumentManager(file -> 3, () -> {});

    OpenDocumentWorkerTest() {
        model.addUndoableEditListener(undoManager);
    }

    private void run(OpenDocumentWorker worker) throws InterruptedException {
        CountDownLatch finished = new CountDownLatch(1);
        worker.addPropertyChangeListener(event -> {
            if (SwingWorker.StateValue.DONE.equals(event.getNewValue())) {
                finished.countDown();
            }
        });
        worker.execute();
        assertThat(finished.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void openingFilesIsOneUndoStep() throws InterruptedException {
        run(new OpenDocumentWorker(documentManager, model, -1, List.of(new File("a.pdf"), new File("b.pdf"))));

        assertThat(model.getSize()).isEqualTo(6);
        undoManager.undo();
        assertThat(model.getSize()).isZero();
        assertThat(undoManager.canUndo()).isFalse();
    }

    @Test
    void workerThatNeverRunsLeavesNoCompoundEditOpen() {
        new OpenDocumentWorker(documentManager, model, -1, List.of(new File("a.pdf")));

        model.add(0, new Page(new File("x.pdf"), 0));

        assertThat(undoManager.canUndo()).isTrue();
    }

    @Test
    void failingWorkerLeavesNoCompoundEditOpen() throws InterruptedException {
        DocumentManager failing = new DocumentManager(
                file -> {
                    throw new java.io.IOException("broken");
                },
                () -> {});

        run(new OpenDocumentWorker(failing, model, -1, List.of(new File("a.pdf"))));
        model.add(0, new Page(new File("x.pdf"), 0));

        assertThat(undoManager.canUndo()).isTrue();
    }
}
