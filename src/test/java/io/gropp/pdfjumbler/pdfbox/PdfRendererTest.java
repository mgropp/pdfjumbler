package io.gropp.pdfjumbler.pdfbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.TestPdfs;
import io.gropp.pdfjumbler.pdf.Page;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfRendererTest {
    @TempDir
    File tempDir;

    private File pdf;
    private PdfRenderer renderer;

    @BeforeEach
    void setUp() throws IOException {
        pdf = TestPdfs.create(new File(tempDir, "doc.pdf"), 3);
        renderer = new PdfRenderer();
    }

    @AfterEach
    void tearDown() throws IOException {
        renderer.close();
    }

    @Test
    void countsPages() throws IOException {
        assertThat(renderer.getNumberOfPages(pdf)).isEqualTo(3);
    }

    @Test
    void rendersPageWithinBoundsKeepingAspectRatio() throws IOException {
        BufferedImage image = (BufferedImage) renderer.renderPage(new Page(pdf, 0), 50, 1000);

        assertThat(image.getWidth()).isEqualTo(50);
        assertThat(image.getHeight()).isEqualTo(100);
    }

    @Test
    void rendersRotatedPageWithSwappedDimensions() throws IOException {
        Page page = new Page(pdf, 0);
        page.setRotation(90);

        BufferedImage image = (BufferedImage) renderer.renderPage(page, 1000, 50);

        assertThat(image.getWidth()).isEqualTo(100);
        assertThat(image.getHeight()).isEqualTo(50);
    }

    @Test
    void rendersSamePageAgainAfterRotationChanges() throws IOException {
        Page page = new Page(pdf, 0);
        renderer.renderPage(page, 50, 1000);

        page.setRotation(270);
        BufferedImage image = (BufferedImage) renderer.renderPage(page, 1000, 50);

        assertThat(image.getWidth()).isEqualTo(100);
        assertThat(image.getHeight()).isEqualTo(50);
    }

    @Test
    void fitsPageWithIntrinsicRotationWithinBounds() throws IOException {
        File rotatedPdf = TestPdfs.createRotated(new File(tempDir, "rotated.pdf"), 90);

        BufferedImage image = (BufferedImage) renderer.renderPage(new Page(rotatedPdf, 0), 1000, 50);

        assertThat(image.getWidth()).isEqualTo(100);
        assertThat(image.getHeight()).isEqualTo(50);
    }

    @Test
    void rejectsUnsupportedRotation() {
        Page page = new Page(pdf, 0);
        page.setRotation(45);

        assertThatThrownBy(() -> renderer.renderPage(page, 100, 100)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void canRenderAgainAfterClose() throws IOException {
        renderer.renderPage(new Page(pdf, 1), 50, 50);
        renderer.close();

        assertThat(renderer.getNumberOfPages(pdf)).isEqualTo(3);
    }
}
