package net.sourceforge.pdfjumbler.pdf;

public interface PdfProcessorListener {
	public void pdfEditorChanged(PdfEditor oldEditor, PdfEditor newEditor);
	public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer);
}
