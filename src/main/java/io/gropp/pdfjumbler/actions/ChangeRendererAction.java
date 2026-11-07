package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import io.gropp.pdfjumbler.ui.Dialogs;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;
import org.tinylog.Logger;

public class ChangeRendererAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = 8330129264136353423L;

    private final Dialogs dialogs;
    private final PdfProcessingFactory processing;
    private final Class<? extends PdfRenderer> rendererClass;

    public ChangeRendererAction(
            Dialogs dialogs, PdfProcessingFactory processing, Class<? extends PdfRenderer> rendererClass) {
        super(PdfProcessingFactory.getFriendlyName(rendererClass));
        this.dialogs = dialogs;
        this.processing = processing;
        this.rendererClass = rendererClass;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            processing.setRendererClass(rendererClass);
        } catch (Exception ex) {
            Logger.error(ex);
            dialogs.showError(ex.getLocalizedMessage(), ex.getClass().getSimpleName());
        }
    }
}
