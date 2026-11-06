package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessorListener;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import java.io.IOException;

public class ReloadingProcessorListener implements PdfProcessorListener {
    private final PdfJumbler parent;

    public ReloadingProcessorListener(PdfJumbler parent) {
        this.parent = parent;
    }

    @Override
    public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer) {
        try {
            oldRenderer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            parent.getMainPdfList().updateUI();
            parent.getSecondaryPdfList().updateUI();
        }
    }

    @Override
    public void pdfEditorChanged(final PdfEditor oldEditor, final PdfEditor newEditor) {
        try {
            oldEditor.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
