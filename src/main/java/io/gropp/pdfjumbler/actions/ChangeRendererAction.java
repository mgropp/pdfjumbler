package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.tinylog.Logger;

public class ChangeRendererAction extends AbstractAction {
    private static final long serialVersionUID = 8330129264136353423L;
    private final PdfJumbler parent;
    private final Class<? extends PdfRenderer> rendererClass;

    public ChangeRendererAction(PdfJumbler parent, Class<? extends PdfRenderer> rendererClass) {
        super(PdfProcessingFactory.getFriendlyName(rendererClass));
        this.parent = parent;
        this.rendererClass = rendererClass;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            PdfProcessingFactory.getInstance().setRendererClass(rendererClass);
        } catch (Exception ex) {
            Logger.error(ex);
            JOptionPane.showMessageDialog(
                    parent, ex.getLocalizedMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
