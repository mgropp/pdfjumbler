package io.gropp.pdfjumbler.testutil;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.pdf.NoPdfProcessorException;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdfbox.PdfBoxEditor;
import io.gropp.pdfjumbler.pdfbox.PdfBoxRenderer;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.io.File;
import java.util.List;

public final class TestPdfLists {
    private TestPdfLists() {}

    public static PdfProcessingFactory processing() {
        try {
            return new PdfProcessingFactory(
                    List.of(PdfBoxEditor.class), List.of(PdfBoxRenderer.class), new InMemoryPreferences(), null, null);
        } catch (NoPdfProcessorException e) {
            throw new IllegalStateException(e);
        }
    }

    public static PdfList pdfList(int pageCount) {
        PdfList list = new PdfList(true, processing(), (target, position, files) -> {});
        for (int i = 0; i < pageCount; i++) {
            list.getModel().add(i, new Page(new File("test.pdf"), i));
        }
        return list;
    }
}
