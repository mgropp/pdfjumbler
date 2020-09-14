package net.sourceforge.pdfjumbler.pdf;

/**
 * @author Martin Gropp
 */
public interface PdfProcessorListener {
	void pdfEditorChanged(PdfEditor oldEditor, PdfEditor newEditor);
	void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer);
}
