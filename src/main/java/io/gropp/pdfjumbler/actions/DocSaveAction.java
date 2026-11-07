package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.ui.Dialogs;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serial;
import java.util.Locale;
import javax.swing.*;

public class DocSaveAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = -1652454884847492822L;

    private final Dialogs dialogs;
    private final FileOperations fileOperations;
    private final PdfList list;

    public DocSaveAction(Dialogs dialogs, FileOperations fileOperations, PdfList list) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.SAVE_DOCUMENT_AS), Icons.DOCUMENT_SAVE);
        this.dialogs = dialogs;
        this.fileOperations = fileOperations;
        this.list = list;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.SAVE_DOCUMENT_AS));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_SAVE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialogs.chooseFileToSave().map(DocSaveAction::withPdfExtension).ifPresent(file -> {
            if (!file.exists() || confirmOverwrite(file)) {
                fileOperations.saveFile(list, file);
            }
        });
    }

    private boolean confirmOverwrite(File file) {
        return dialogs.confirmWarning(
                String.format(
                        PdfJumblerResources.getResources().getString(I18nKeys.OVERWRITE_FILE_TEXT),
                        file.getAbsolutePath()),
                PdfJumblerResources.getResources().getString(I18nKeys.OVERWRITE_FILE_TITLE));
    }

    private static File withPdfExtension(File file) {
        return file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")
                ? file
                : new File(file.getAbsolutePath() + ".pdf");
    }
}
