package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.pdf.PdfProcessingFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AboutAction extends AbstractAction {
	private static final long serialVersionUID = -6505580153294146608L;
	private final PdfJumbler parent;

	public AboutAction(PdfJumbler parent) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.ABOUT));
		this.parent = parent;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ABOUT));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(
			parent,
			String.format(
				PdfJumblerResources.getResources().getString(I18nKeys.ABOUT_TEXT),
				PdfJumbler.VERSION_STRING,
				PdfProcessingFactory.getEditor().getClass().getCanonicalName(),
				PdfProcessingFactory.getRenderer().getClass().getCanonicalName()
			),
			PdfJumblerResources.getResources().getString(I18nKeys.ABOUT_TITLE),
			JOptionPane.INFORMATION_MESSAGE
		);
	}
}
