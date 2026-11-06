package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import org.tinylog.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeEditorAction extends AbstractAction {
	private static final long serialVersionUID = -8468488158161906330L;
	private final PdfJumbler parent;
	private final Class<? extends PdfEditor> editorClass;

	public ChangeEditorAction(PdfJumbler parent, Class<? extends PdfEditor> editorClass) {
		super(PdfProcessingFactory.getFriendlyName(editorClass));
		this.parent = parent;
		this.editorClass = editorClass;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			PdfProcessingFactory.setEditorClass(editorClass);
		}
		catch (Exception ex) {
			Logger.error(ex);
			JOptionPane.showMessageDialog(parent, ex.getLocalizedMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		}
	}
}
