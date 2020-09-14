package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.pdf.PdfProcessingFactory;
import net.sourceforge.pdfjumbler.pdf.PdfRenderer;
import org.tinylog.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeRendererAction extends AbstractAction {
	private static final long serialVersionUID = 8330129264136353423L;
	private final PdfJumbler parent;
	private final Class<? extends PdfRenderer> rendererClass;

	public ChangeRendererAction(PdfJumbler parent, Class<? extends PdfRenderer> rendererClass) {
		super(PdfProcessingFactory.getFriendlyName(rendererClass));
		this.parent = parent;
		this.rendererClass = rendererClass;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			PdfProcessingFactory.setRendererClass(rendererClass);
		}
		catch (Exception ex) {
			Logger.error(ex);
			JOptionPane.showMessageDialog(parent, ex.getLocalizedMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		}
	}
}
