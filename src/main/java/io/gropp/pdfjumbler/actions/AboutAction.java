package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.AppVersion;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;

public class AboutAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = -6505580153294146608L;

    private final Component parent;
    private final PdfProcessingFactory processing;

    public AboutAction(Component parent, PdfProcessingFactory processing) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.ABOUT));
        this.parent = parent;
        this.processing = processing;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ABOUT));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(
                parent,
                String.format(
                        PdfJumblerResources.getResources().getString(I18nKeys.ABOUT_TEXT),
                        AppVersion.current(),
                        processing.getEditor().getClass().getCanonicalName(),
                        processing.getRenderer().getClass().getCanonicalName()),
                PdfJumblerResources.getResources().getString(I18nKeys.ABOUT_TITLE),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
