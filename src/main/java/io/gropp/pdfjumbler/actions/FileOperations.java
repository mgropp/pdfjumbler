package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.ui.Dialogs;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.io.File;
import java.util.Collection;

public class FileOperations {
    private final Dialogs dialogs;
    private final WorkerRunner workerRunner;
    private final DocumentManager documentManager;
    private final PdfProcessingFactory processing;

    public FileOperations(
            Dialogs dialogs,
            WorkerRunner workerRunner,
            DocumentManager documentManager,
            PdfProcessingFactory processing) {
        this.dialogs = dialogs;
        this.workerRunner = workerRunner;
        this.documentManager = documentManager;
        this.processing = processing;
    }

    public void openFiles(PdfList list, int insertPos, Collection<File> files) {
        if (!files.isEmpty()) {
            try {
                workerRunner.run(new OpenDocumentWorker(documentManager, list.getModel(), insertPos, files));
            } catch (Exception e) {
                dialogs.showError(e);
            }
        }
    }

    public void saveFile(PdfList list, File file) {
        try {
            workerRunner.run(new SaveDocumentWorker(documentManager, processing, list.getModel(), file));
        } catch (Exception e) {
            dialogs.showError(e);
        }
    }
}
