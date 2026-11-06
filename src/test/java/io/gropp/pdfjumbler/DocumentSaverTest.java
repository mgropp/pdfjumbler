package io.gropp.pdfjumbler;

import static io.gropp.pdfjumbler.TestPdfs.pageInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.pdf.Page;
import io.gropp.pdfjumbler.pdfbox.PdfEditor;
import io.gropp.pdfjumbler.pdfbox.PdfRenderer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocumentSaverTest {
    @TempDir
    File tempDir;

    private final PdfRenderer renderer = new PdfRenderer();
    private final DocumentManager manager = new DocumentManager(renderer::getNumberOfPages, renderer::close);
    private final PdfEditor editor = new PdfEditor();
    private final DocumentSaver saver = new DocumentSaver(manager, editor::saveDocument);

    @Test
    void savingNoPagesLeavesTheOpenOriginalUntouched() throws IOException {
        File file = TestPdfs.create(new File(tempDir, "doc.pdf"), 2);
        manager.getPages(file);

        assertThatThrownBy(() -> saver.save(List.of(), file, () -> false)).isInstanceOf(IOException.class);

        assertThat(TestPdfs.read(file)).hasSize(2);
        assertThat(tempDir.list()).containsExactly("doc.pdf");
    }

    @Test
    void failedWriteLeavesTheOpenOriginalUntouched() throws IOException {
        File file = TestPdfs.create(new File(tempDir, "doc.pdf"), 2);
        List<Page> pages = manager.getPages(file);
        DocumentSaver failing = new DocumentSaver(manager, (p, f) -> {
            throw new IOException("disk full");
        });

        assertThatThrownBy(() -> failing.save(pages, file, () -> false)).hasMessage("disk full");

        assertThat(TestPdfs.read(file)).hasSize(2);
        assertThat(pages).allSatisfy(page -> assertThat(page.getFile()).isEqualTo(file));
        assertThat(tempDir.list()).containsExactly("doc.pdf");
    }

    @Test
    void cancelledSaveLeavesTheOriginalUntouched() throws IOException {
        File file = TestPdfs.create(new File(tempDir, "doc.pdf"), 2);
        List<Page> pages = manager.getPages(file);

        assertThatThrownBy(() -> saver.save(pages.subList(0, 1), file, () -> true))
                .isInstanceOf(CancellationException.class);

        assertThat(TestPdfs.read(file)).hasSize(2);
        assertThat(tempDir.list()).containsExactly("doc.pdf");
    }

    @Test
    void overwritingAnOpenFileKeepsItsPagesReadable() throws IOException {
        File file = TestPdfs.create(new File(tempDir, "doc.pdf"), 3);
        List<Page> pages = manager.getPages(file);

        saver.save(List.of(pages.get(2), pages.get(0)), file, () -> false);

        assertThat(TestPdfs.read(file)).containsExactly(pageInfo(2, 0), pageInfo(0, 0));
        assertThat(pages.get(1).getFile()).isNotEqualTo(file);
        assertThat(Files.exists(pages.get(1).getFile().toPath())).isTrue();
        assertThat(tempDir.list()).containsExactly("doc.pdf");
    }
}
