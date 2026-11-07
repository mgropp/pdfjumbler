package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.ui.Dialogs;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;
import org.tinylog.Logger;

public class ChangeEditorAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = -8468488158161906330L;

    private final Dialogs dialogs;
    private final PdfProcessingFactory processing;
    private final Class<? extends PdfEditor> editorClass;

    public ChangeEditorAction(
            Dialogs dialogs, PdfProcessingFactory processing, Class<? extends PdfEditor> editorClass) {
        super(PdfProcessingFactory.getFriendlyName(editorClass));
        this.dialogs = dialogs;
        this.processing = processing;
        this.editorClass = editorClass;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            processing.setEditorClass(editorClass);
        } catch (Exception ex) {
            Logger.error(ex);
            dialogs.showError(ex.getLocalizedMessage(), ex.getClass().getSimpleName());
        }
    }
}
