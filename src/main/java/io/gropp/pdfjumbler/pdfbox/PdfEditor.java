package io.gropp.pdfjumbler.pdfbox;

import io.gropp.pdfjumbler.pdf.Page;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.tinylog.Logger;

/**
 * PdfJumbler interface to PDFBox.
 *
 * @author Martin Gropp
 */
public class PdfEditor implements io.gropp.pdfjumbler.pdf.PdfEditor {
    public static String getFriendlyName() {
        return "PDFBox";
    }

    @Override
    public void close() {}

    @Override
    public void saveDocument(List<Page> pages, File file) throws IOException {
        Logger.info("Saving {} pages to {}", pages.size(), file);

        if (pages.size() == 0) {
            throw new IOException("Empty document.");
        }

        Map<File, PDDocument> docs = new HashMap<>();
        try (PDDocument outDoc = new PDDocument()) {
            for (Page page : pages) {
                PDDocument pageDoc = docs.get(page.getFile());
                if (pageDoc == null) {
                    pageDoc = Loader.loadPDF(page.getFile());
                    docs.put(page.getFile(), pageDoc);
                }

                PDPage sourcePage = pageDoc.getPage(page.getIndex());
                outDoc.addPage(copyWithInheritedAttributes(sourcePage, page.getRotation()));
            }

            outDoc.save(file.toString());
            Logger.info("File saved successfully.");
        } finally {
            for (PDDocument doc : docs.values()) {
                doc.close();
            }
        }
    }

    private static PDPage copyWithInheritedAttributes(PDPage source, int additionalRotation) {
        PDPage copy = new PDPage(new COSDictionary(source.getCOSObject()));
        copy.setMediaBox(source.getMediaBox());
        copy.setCropBox(source.getCropBox());
        PDResources resources = source.getResources();
        if (resources != null) {
            copy.setResources(resources);
        }
        copy.setRotation((source.getRotation() + additionalRotation) % 360);
        return copy;
    }
}
