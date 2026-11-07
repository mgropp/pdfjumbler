package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.ui.Dialogs;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;

public class DocOpenAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = -1652454884847492822L;

    private final Dialogs dialogs;
    private final FileOperations fileOperations;
    private final PdfList list;

    public DocOpenAction(Dialogs dialogs, FileOperations fileOperations, PdfList list) {
        this(dialogs, fileOperations, list, Icons.DOCUMENT_OPEN);
    }

    public DocOpenAction(Dialogs dialogs, FileOperations fileOperations, PdfList list, Icon icon) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.OPEN_DOCUMENT), icon);
        this.dialogs = dialogs;
        this.fileOperations = fileOperations;
        this.list = list;

        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.OPEN_DOCUMENT));
        putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_OPEN));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialogs.chooseFilesToOpen().ifPresent(files -> fileOperations.openFiles(list, -1, files));
    }
}
