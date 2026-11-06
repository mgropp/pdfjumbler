package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.DocumentManager;
import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import io.gropp.pdfjumbler.pdf.Page;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class SaveDocumentWorker extends SwingWorker<Void,Void> {
	private final List<Page> pages;
	private final File file;

	public SaveDocumentWorker(UndoableListModel<Page> model, File file) {
		this.pages = model.getList();
		this.file = file;
	}

	@Override
	protected Void doInBackground() throws Exception {
		firePropertyChange("note", "", file.getName());

		// TODO: Detect links! (java.nio.file?)

		if (file.exists() && DocumentManager.getAllFiles().contains(file)) {
			File tempFile = File.createTempFile("pdfjumbler", ".pdf");
			Files.move(file.toPath(), tempFile.toPath());
			tempFile.deleteOnExit();

			for (Page page : DocumentManager.getPages(file)) {
				page.setFile(tempFile);
			}
		}

		PdfProcessingFactory.getEditor().saveDocument(
			pages,
			file
		);

		return null;
	}
}
