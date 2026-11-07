package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.testutil.RecordingDialogs;
import io.gropp.pdfjumbler.testutil.SynchronousWorkers;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.testutil.TestPdfs;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileOperationsTest {
    private final RecordingDialogs dialogs = new RecordingDialogs();
    private final AtomicInteger runs = new AtomicInteger();
    private final PdfList list = TestPdfLists.pdfList(0);

    @TempDir
    Path directory;

    private FileOperations fileOperations(WorkerRunner runner) {
        return new FileOperations(dialogs, runner, new DocumentManager(file -> 2, () -> {}), TestPdfLists.processing());
    }

    private FileOperations synchronousFileOperations() {
        return fileOperations(worker -> {
            runs.incrementAndGet();
            return SynchronousWorkers.run(worker);
        });
    }

    @Test
    void openFilesAddsAllPagesOfAllFiles() {
        synchronousFileOperations().openFiles(list, -1, List.of(new File("a.pdf"), new File("b.pdf")));

        assertThat(list.getModel().getSize()).isEqualTo(4);
        assertThat(dialogs.errors).isEmpty();
    }

    @Test
    void openingNoFilesDoesNotRunAWorker() {
        synchronousFileOperations().openFiles(list, -1, List.of());

        assertThat(runs).hasValue(0);
    }

    @Test
    void failureWhileOpeningIsShownToTheUser() {
        fileOperations(worker -> {
                    throw new IOException("cannot open");
                })
                .openFiles(list, -1, List.of(new File("a.pdf")));

        assertThat(dialogs.errors).singleElement().satisfies(error -> {
            assertThat(error.title()).isEqualTo("IOException");
            assertThat(error.text()).isEqualTo("cannot open");
        });
    }

    @Test
    void saveFileWritesPagesToTarget() throws IOException {
        File source = TestPdfs.create(directory.resolve("source.pdf").toFile(), 2);
        list.getModel().add(0, new Page(source, 1));
        File target = directory.resolve("out.pdf").toFile();

        synchronousFileOperations().saveFile(list, target);

        assertThat(TestPdfs.read(target)).containsExactly(TestPdfs.pageInfo(1, 0));
        assertThat(dialogs.errors).isEmpty();
    }

    @Test
    void failureWhileSavingIsShownToTheUser() {
        fileOperations(worker -> {
                    throw new IOException("disk full");
                })
                .saveFile(list, directory.resolve("out.pdf").toFile());

        assertThat(dialogs.errors).singleElement().satisfies(error -> assertThat(error.text())
                .isEqualTo("disk full"));
    }
}
