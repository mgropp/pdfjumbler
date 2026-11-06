package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.Icons;
import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.PdfList;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.jspecify.annotations.Nullable;

public class DocOpenAction extends AbstractAction {
    private static final long serialVersionUID = -1652454884847492822L;

    private static @Nullable String lastOpenedFileLocation;
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
                return file.isDirectory()
                        || (file.exists()
                                && file.isFile()
                                && file.canRead()
                                && file.getName().toLowerCase().endsWith(".pdf"));
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
