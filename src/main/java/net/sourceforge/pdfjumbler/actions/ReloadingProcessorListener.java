package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.pdf.PdfEditor;
import net.sourceforge.pdfjumbler.pdf.PdfProcessorListener;
import net.sourceforge.pdfjumbler.pdf.PdfRenderer;

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
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			parent.getMainPdfList().updateUI();
			parent.getSecondaryPdfList().updateUI();
		}
	}

	@Override
	public void pdfEditorChanged(final PdfEditor oldEditor, final PdfEditor newEditor) {
		try {
			oldEditor.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
