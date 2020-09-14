package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.DocumentManager;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableListModel;
import net.sourceforge.pdfjumbler.pdf.Page;
import org.tinylog.Logger;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenDocumentWorker extends SwingWorker<Void, List<Page>> {
	private final UndoableListModel<Page> model;
	private final AtomicInteger insertPos;
	private final Collection<File> files;

	public OpenDocumentWorker(UndoableListModel<Page> model, int insertPos, Collection<File> files) {
		this.model = model;
		this.insertPos = new AtomicInteger((insertPos < 0) ? model.getSize() : insertPos);
		this.files = files;

		// execute is final :/
		model.beginCompoundEdit("Open");
	}

	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);

		int nFiles = 0;
		for (File file : files) {
			Logger.info("Opening file: {}", file);
			firePropertyChange("note", "", file.getName());

			publish(DocumentManager.getPages(file));

			nFiles++;
			setProgress((int)(100 * (nFiles / (double)files.size())));
		}

		return null;
	}

	@Override
	protected void process(List<List<Page>> pages) {
		for (List<Page> pageList : pages) {
			for (Page page : pageList) {
				model.add(
					insertPos.getAndIncrement(),
					page
				);
			}
		}
	}

	@Override
	protected void done() {
		model.endCompoundEdit();
	}
}
