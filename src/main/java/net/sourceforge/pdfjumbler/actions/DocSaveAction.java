package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.Icons;
import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.PdfList;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;

public class DocSaveAction extends AbstractAction {
	private static final long serialVersionUID = -1652454884847492822L;
	private static String lastSavedLocation = null;
	private final PdfJumbler parent;
	private final PdfList list;

	public DocSaveAction(PdfJumbler parent, PdfList list) {
		this(parent, list, Icons.DOCUMENT_SAVE);
	}

	public DocSaveAction(PdfJumbler parent, PdfList list, Icon icon) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.SAVE_DOCUMENT), icon);
		this.parent = parent;
		this.list = list;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.SAVE_DOCUMENT));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_SAVE));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(DocSaveAction.lastSavedLocation);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return
					file.isDirectory() ||
					((file.getParentFile() != null) && file.getParentFile().exists()) &&
					(
						(!file.exists() ||
						file.isFile()) && file.canWrite() && file.getName().toLowerCase().endsWith(".pdf")
					);
			}

			@Override
			public String getDescription() {
				return PdfJumblerResources.getResources().getString(I18nKeys.FILE_FILTER_PDF);
			}
		});

		if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".pdf")) {
				file = new File(file.getAbsolutePath() + ".pdf");
			}
			if (
				file.exists() &&
				(JOptionPane.showConfirmDialog(
					parent,
					String.format(PdfJumblerResources.getResources().getString(I18nKeys.OVERWRITE_FILE_TEXT), file.getAbsolutePath()),
					PdfJumblerResources.getResources().getString(I18nKeys.OVERWRITE_FILE_TITLE),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE
				) != JOptionPane.OK_OPTION)
			) {
				return;
			}
			DocSaveAction.lastSavedLocation = file.getParent();
			PdfJumbler.saveFile(list, file);
		}
	}
}
