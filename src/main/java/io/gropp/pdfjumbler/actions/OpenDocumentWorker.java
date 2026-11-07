package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import org.tinylog.Logger;

public class OpenDocumentWorker extends SwingWorker<Void, List<Page>> {
    private final DocumentManager documentManager;
    private final UndoableListModel<Page> model;
    private final AtomicInteger insertPos;
    private final Collection<File> files;
    private boolean editStarted;

    public OpenDocumentWorker(
            DocumentManager documentManager, UndoableListModel<Page> model, int insertPos, Collection<File> files) {
        this.documentManager = documentManager;
        this.model = model;
        this.insertPos = new AtomicInteger((insertPos < 0) ? model.getSize() : insertPos);
        this.files = files;
    }

    @Override
    protected Void doInBackground() throws Exception {
        setProgress(0);

        int nFiles = 0;
        for (File file : files) {
            Logger.info("Opening file: {}", file);
            firePropertyChange("note", "", file.getName());

            publish(documentManager.getPages(file));

            nFiles++;
            setProgress((int) (100 * (nFiles / (double) files.size())));
        }

        return null;
    }

    @Override
    protected void process(List<List<Page>> pages) {
        if (!editStarted) {
            model.beginCompoundEdit("Open");
            editStarted = true;
        }
        for (List<Page> pageList : pages) {
            for (Page page : pageList) {
                model.add(insertPos.getAndIncrement(), page);
            }
        }
    }

    @Override
    protected void done() {
        if (editStarted) {
            model.endCompoundEdit();
        }
    }
}
