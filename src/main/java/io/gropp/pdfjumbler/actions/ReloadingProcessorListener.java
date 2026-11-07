package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessorListener;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.io.IOException;

public class ReloadingProcessorListener implements PdfProcessorListener {
    private final PdfList mainList;
    private final PdfList secondaryList;

    public ReloadingProcessorListener(PdfList mainList, PdfList secondaryList) {
        this.mainList = mainList;
        this.secondaryList = secondaryList;
    }

    @Override
    public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer) {
        try {
            oldRenderer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            mainList.updateUI();
            secondaryList.updateUI();
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
