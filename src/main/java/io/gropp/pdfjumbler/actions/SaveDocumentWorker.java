package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.document.DocumentSaver;
import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import java.io.File;
import java.util.List;
import javax.swing.*;

public class SaveDocumentWorker extends SwingWorker<Void, Void> {
    private final DocumentManager documentManager;
    private final PdfProcessingFactory processing;
    private final List<Page> pages;
    private final File file;

    public SaveDocumentWorker(
            DocumentManager documentManager,
            PdfProcessingFactory processing,
            UndoableListModel<Page> model,
            File file) {
        this.documentManager = documentManager;
        this.processing = processing;
        this.pages = model.getList();
        this.file = file;
    }

    @Override
    protected Void doInBackground() throws Exception {
        firePropertyChange("note", "", file.getName());

        DocumentSaver saver = new DocumentSaver(
                documentManager, (pageList, target) -> processing.getEditor().saveDocument(pageList, target));
        saver.save(pages, file, this::isCancelled);

        return null;
    }
}
