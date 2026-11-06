package io.gropp.pdfjumbler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.pdf.Page;
import io.gropp.pdfjumbler.pdfbox.PdfEditor;
import io.gropp.pdfjumbler.pdfbox.PdfRenderer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocumentManagerTest {
    @TempDir
    File tempDir;

    private final List<File> countedFiles = new ArrayList<>();
    private final AtomicInteger closeCalls = new AtomicInteger();
    private final DocumentManager manager = new DocumentManager(
            file -> {
                countedFiles.add(file);
                return 3;
            },
            closeCalls::incrementAndGet);

    @Test
    void createsOnePagePerPageOfTheFile() throws IOException {
        File file = new File(tempDir, "doc.pdf");

        List<Page> pages = manager.getPages(file);

        assertThat(pages).containsExactly(new Page(file, 0), new Page(file, 1), new Page(file, 2));
    }

    @Test
    void countsPagesOnlyOncePerFile() throws IOException {
        File file = new File(tempDir, "doc.pdf");

        List<Page> first = manager.getPages(file);
        List<Page> second = manager.getPages(file);

        assertThat(second).isEqualTo(first);
        assertThat(countedFiles).containsExactly(file);
    }

    @Test
    void returnsIndependentPagesWhenFileIsOpenedAgain() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        List<Page> first = manager.getPages(file);
        List<Page> second = manager.getPages(file);

        first.get(0).setRotation(90);

        assertThat(second.get(0).getRotation()).isZero();
    }

    @Test
    void moveAsideRedirectsPagesOfEveryOpening() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        Files.writeString(file.toPath(), "original");
        List<Page> first = manager.getPages(file);
        List<Page> second = manager.getPages(file);

        manager.moveAsideIfOpen(file);

        File movedFile = first.get(0).getFile();
        assertThat(movedFile).isNotEqualTo(file);
        assertThat(second).allSatisfy(page -> assertThat(page.getFile()).isEqualTo(movedFile));
    }

    @Test
    void returnedPagesAreUnmodifiable() throws IOException {
        List<Page> pages = manager.getPages(new File(tempDir, "doc.pdf"));

        assertThatThrownBy(() -> pages.remove(0)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void propagatesPageCountFailure() {
        DocumentManager failing = new DocumentManager(
                file -> {
                    throw new IOException("broken");
                },
                () -> {});

        assertThatThrownBy(() -> failing.getPages(new File(tempDir, "doc.pdf"))).hasMessage("broken");
        assertThat(failing.getAllFiles()).isEmpty();
    }

    @Test
    void tracksOpenedFiles() throws IOException {
        File first = new File(tempDir, "a.pdf");
        File second = new File(tempDir, "b.pdf");
        manager.getPages(first);
        manager.getPages(second);

        assertThat(manager.getAllFiles()).containsExactlyInAnyOrder(first, second);
    }

    @Test
    void moveAsideIgnoresFilesThatAreNotOpen() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        Files.writeString(file.toPath(), "content");

        manager.moveAsideIfOpen(file);

        assertThat(file).hasContent("content");
    }

    @Test
    void moveAsideIgnoresOpenFilesThatNoLongerExist() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        List<Page> pages = manager.getPages(file);

        manager.moveAsideIfOpen(file);

        assertThat(pages).allSatisfy(page -> assertThat(page.getFile()).isEqualTo(file));
    }

    @Test
    void moveAsideMovesOpenFileAndRedirectsItsPages() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        Files.writeString(file.toPath(), "original");
        List<Page> pages = manager.getPages(file);

        manager.moveAsideIfOpen(file);

        assertThat(file).doesNotExist();
        File movedFile = pages.get(0).getFile();
        assertThat(movedFile).isNotEqualTo(file).hasContent("original");
        assertThat(pages).allSatisfy(page -> assertThat(page.getFile()).isEqualTo(movedFile));
    }

    @Test
    void reopeningOverwrittenFileCreatesFreshPages() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        Files.writeString(file.toPath(), "original");
        List<Page> oldPages = manager.getPages(file);

        manager.moveAsideIfOpen(file);
        Files.writeString(file.toPath(), "saved");
        List<Page> newPages = manager.getPages(file);

        assertThat(newPages).allSatisfy(page -> assertThat(page.getFile()).isEqualTo(file));
        assertThat(manager.getAllFiles())
                .containsExactlyInAnyOrder(file, oldPages.get(0).getFile());
        assertThat(manager.getPages(oldPages.get(0).getFile())).isEqualTo(oldPages);
        assertThat(countedFiles).containsExactly(file, file);
    }

    @Test
    void moveAsideClosesOpenFilesBeforeMoving() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        Files.writeString(file.toPath(), "original");
        List<Boolean> existedWhenClosing = new ArrayList<>();
        DocumentManager checking = new DocumentManager(f -> 1, () -> existedWhenClosing.add(file.exists()));
        checking.getPages(file);

        checking.moveAsideIfOpen(file);

        assertThat(existedWhenClosing).containsExactly(true);
    }

    @Test
    void moveAsideDoesNotCloseFilesWhenNothingIsMoved() throws IOException {
        File file = new File(tempDir, "doc.pdf");
        Files.writeString(file.toPath(), "content");

        manager.moveAsideIfOpen(file);

        assertThat(closeCalls).hasValue(0);
    }

    @Test
    void reopeningOverwrittenPdfReadsNewContent() throws IOException {
        File file = TestPdfs.create(new File(tempDir, "doc.pdf"), 3);
        PdfRenderer renderer = new PdfRenderer();
        DocumentManager pdfManager = new DocumentManager(renderer::getNumberOfPages, renderer::close);
        List<Page> oldPages = pdfManager.getPages(file);

        pdfManager.moveAsideIfOpen(file);
        new PdfEditor().saveDocument(List.of(oldPages.get(2)), file);

        assertThat(pdfManager.getPages(file)).hasSize(1);
        assertThat(pdfManager.getPages(oldPages.get(0).getFile())).hasSize(3);
        renderer.close();
    }
}
