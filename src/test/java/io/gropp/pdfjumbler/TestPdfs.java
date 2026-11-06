package io.gropp.pdfjumbler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public final class TestPdfs {
    private TestPdfs() {}

    public static float widthOfPage(int index) {
        return 100 + 10 * index;
    }

    public static float heightOfPage(int index) {
        return 2 * widthOfPage(index);
    }

    public static File create(File file, int pageCount) throws IOException {
        return create(file, pageCount, 0);
    }

    public static File create(File file, int pageCount, int firstPageIndex) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (int i = 0; i < pageCount; i++) {
                int index = firstPageIndex + i;
                document.addPage(new PDPage(new PDRectangle(widthOfPage(index), heightOfPage(index))));
            }
            document.save(file);
        }
        return file;
    }

    public static File createRotated(File file, int intrinsicRotation) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(widthOfPage(0), heightOfPage(0)));
            page.setRotation(intrinsicRotation);
            document.addPage(page);
            document.save(file);
        }
        return file;
    }

    public record PageInfo(float width, float height, int rotation) {}

    public static List<PageInfo> read(File file) throws IOException {
        List<PageInfo> pages = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(file)) {
            for (PDPage page : document.getPages()) {
                PDRectangle box = page.getMediaBox();
                pages.add(new PageInfo(box.getWidth(), box.getHeight(), page.getRotation()));
            }
        }
        return pages;
    }

    public static PageInfo pageInfo(int index, int rotation) {
        return new PageInfo(widthOfPage(index), heightOfPage(index), rotation);
    }
}
