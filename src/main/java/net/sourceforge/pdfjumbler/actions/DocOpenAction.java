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
import java.util.Arrays;

public class DocOpenAction extends AbstractAction {
	private static final long serialVersionUID = -1652454884847492822L;

	private static String lastOpenedFileLocation = null;
	private final PdfJumbler parent;
	private final PdfList list;

	public DocOpenAction(PdfJumbler parent, PdfList list) {
		this(parent, list, Icons.DOCUMENT_OPEN);
	}

	public DocOpenAction(PdfJumbler parent, PdfList list, Icon icon) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.OPEN_DOCUMENT), icon);
		this.parent = parent;
		this.list = list;

		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.OPEN_DOCUMENT));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_OPEN));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(DocOpenAction.lastOpenedFileLocation);
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				DocOpenAction.lastOpenedFileLocation = file.getParent();
				return
					file.isDirectory() ||
					(file.exists() && file.isFile() && file.canRead() && file.getName().toLowerCase().endsWith(".pdf"));
			}

			@Override
			public String getDescription() {
				return PdfJumblerResources.getResources().getString(I18nKeys.FILE_FILTER_PDF);
			}

		});

		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			PdfJumbler.openFiles(list, -1, Arrays.asList(chooser.getSelectedFiles()));
		}
	}
}
