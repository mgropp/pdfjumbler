package io.gropp.pdfjumbler.pdf;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

public interface PdfRenderer {
    /**
     * Render a page from a PDF document as an image.
     * Currently, it is assumed that PDF editor and renderer are independent,
     * so this method should not rely on the document being open in a particular
     * editor. Also, even if the document is opened in a certain editor when the
     * renderer is initialized, the editor might change.
     * maxWidth and maxHeight specify the maximum size in the respective dimension;
     * due to the fixed aspect ratio, the actual size might be lower.
     *
     * @param page
     *   The page to be renderered.
     * @param maxWidth
     *   The maximum image width.
     * @param maxHeight
     *   The maximum image height.
     * @return
     *   An Image of the PDF page.
     */
    Image renderPage(Page page, int maxWidth, int maxHeight) throws IOException;

    /**
     * Return the number of pages in a PDF file.
     *
     * @return
     *   The number of pages in the file.
     */
    int getNumberOfPages(File file) throws IOException;

    /**
     * Close the editor, for example because a different editor is selected.
     */
    void close() throws IOException;
}
