package io.gropp.pdfjumbler.pdfbox;

import static io.gropp.pdfjumbler.testutil.TestPdfs.pageInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.testutil.TestPdfs;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfBoxEditorTest {
    @TempDir
    File tempDir;

    private final PdfBoxEditor editor = new PdfBoxEditor();

    @Test
    void savesPagesInGivenOrder() throws IOException {
        File source = TestPdfs.create(new File(tempDir, "source.pdf"), 3);
        File target = new File(tempDir, "target.pdf");

        editor.saveDocument(List.of(new Page(source, 2), new Page(source, 0)), target);

        assertThat(TestPdfs.read(target)).containsExactly(pageInfo(2, 0), pageInfo(0, 0));
    }

    @Test
    void savesPageRotation() throws IOException {
        File source = TestPdfs.create(new File(tempDir, "source.pdf"), 1);
        File target = new File(tempDir, "target.pdf");
        Page page = new Page(source, 0);
        page.setRotation(270);

        editor.saveDocument(List.of(page), target);

        assertThat(TestPdfs.read(target)).containsExactly(pageInfo(0, 270));
    }

    @Test
    void combinesPagesFromSeveralFiles() throws IOException {
        File first = TestPdfs.create(new File(tempDir, "first.pdf"), 2);
        File second = TestPdfs.create(new File(tempDir, "second.pdf"), 2, 5);
        File target = new File(tempDir, "target.pdf");

        editor.saveDocument(List.of(new Page(second, 1), new Page(first, 0), new Page(second, 0)), target);

        assertThat(TestPdfs.read(target)).containsExactly(pageInfo(6, 0), pageInfo(0, 0), pageInfo(5, 0));
    }

    @Test
    void savesSamePageTwice() throws IOException {
        File source = TestPdfs.create(new File(tempDir, "source.pdf"), 2);
        File target = new File(tempDir, "target.pdf");

        editor.saveDocument(List.of(new Page(source, 1), new Page(source, 1)), target);

        assertThat(TestPdfs.read(target)).containsExactly(pageInfo(1, 0), pageInfo(1, 0));
    }

    @Test
    void savesDifferentRotationsOfSamePage() throws IOException {
        File source = TestPdfs.create(new File(tempDir, "source.pdf"), 1);
        File target = new File(tempDir, "target.pdf");
        Page rotated = new Page(source, 0);
        rotated.setRotation(90);

        editor.saveDocument(List.of(new Page(source, 0), rotated), target);

        assertThat(TestPdfs.read(target)).containsExactly(pageInfo(0, 0), pageInfo(0, 90));
    }

    @Test
    void addsRotationToSourcePageRotation() throws IOException {
        File source = TestPdfs.createRotated(new File(tempDir, "source.pdf"), 90);
        File target = new File(tempDir, "target.pdf");
        Page unrotated = new Page(source, 0);
        Page rotated = new Page(source, 0);
        rotated.setRotation(270);

        editor.saveDocument(List.of(unrotated, rotated), target);

        assertThat(TestPdfs.read(target)).containsExactly(pageInfo(0, 90), pageInfo(0, 0));
    }

    @Test
    void keepsAttributesInheritedFromPageTree() throws IOException {
        PDRectangle cropBox = new PDRectangle(10, 20, 30, 40);
        PDResources resources = new PDResources();
        COSName fontName = resources.add(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        File source = TestPdfs.createWithInheritedAttributes(new File(tempDir, "source.pdf"), cropBox, resources);
        File target = new File(tempDir, "target.pdf");

        editor.saveDocument(List.of(new Page(source, 0)), target);

        assertThat(TestPdfs.read(target)).containsExactly(pageInfo(0, 0));
        try (PDDocument saved = Loader.loadPDF(target)) {
            PDPage page = saved.getPage(0);
            assertThat(page.getCropBox().toString()).isEqualTo(cropBox.toString());
            assertThat(page.getResources().getFont(fontName)).isNotNull();
        }
    }

    @Test
    void rejectsEmptyDocument() {
        assertThatThrownBy(() -> editor.saveDocument(List.of(), new File(tempDir, "target.pdf")))
                .isInstanceOf(IOException.class);
    }
}
