package io.gropp.pdfjumbler.document;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import java.io.File;
import org.jspecify.annotations.Nullable;

/**
 * Represents a page from a PDF file.
 *
 * @author Martin Gropp
 */
public final class Page {
    private File file;
    private int index;
    private int rotation = 0;

    public Page(File file, int index) {
        this.file = file;
        this.index = index;
    }

    /**
     * Get the file this page originates from.
     *
     * @return
     *   the page's source file
     */
    public File getFile() {
        return file;
    }

    /**
     * Set the file this page originates from.
     *
     * @param file
     *   the page's source file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Return the page number w.r.t. the original file.
     *
     * @return
     *   the original page number (0 based)
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the page number w.r.t. the original file.
     *
     * @param index
     *   the original page number (0 based)
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get the rotation angle.
     *
     * @return
     *   the rotation angle in degrees
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Set the rotation angle.
     * Supported values: 0, 90, 180, 270 (degrees)
     *
     * @param rotation
     *   the rotation angle in degrees
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return o instanceof Page other && file.equals(other.file) && index == other.index && rotation == other.rotation;
    }

    @Override
    public int hashCode() {
        return 32 * (32 * file.hashCode() + index) + rotation;
    }

    @Override
    public String toString() {
        return String.format(
                PdfJumblerResources.getResources().getString(I18nKeys.PDF_PAGE_TITLE), index + 1, file.getName());
    }
}
