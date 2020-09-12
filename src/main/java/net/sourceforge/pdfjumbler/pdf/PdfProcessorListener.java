package net.sourceforge.pdfjumbler.pdf;

/**
 * @author Martin Gropp
 */
public interface PdfProcessorListener {
	public void pdfEditorChanged(PdfEditor oldEditor, PdfEditor newEditor);
	public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer);
}
