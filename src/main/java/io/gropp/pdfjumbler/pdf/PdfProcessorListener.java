package io.gropp.pdfjumbler.pdf;

public interface PdfProcessorListener {
    void pdfEditorChanged(PdfEditor oldEditor, PdfEditor newEditor);

    void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer);
}
