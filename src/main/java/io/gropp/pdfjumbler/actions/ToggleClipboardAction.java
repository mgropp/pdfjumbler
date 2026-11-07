package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.ui.Icons;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JSplitPane;

public class ToggleClipboardAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = 7357355236947950441L;

    private final JSplitPane splitPane;

    public ToggleClipboardAction(JSplitPane splitPane) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.TOGGLE_CLIPBOARD), Icons.CLIPBOARD);
        this.splitPane = splitPane;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.TOGGLE_CLIPBOARD));
        putValue(Action.SELECTED_KEY, isClipboardVisible());
        splitPane.addPropertyChangeListener(
                JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> putValue(Action.SELECTED_KEY, isClipboardVisible()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isClipboardVisible()) {
            splitPane.setDividerLocation(0);
        } else {
            Component clipboardPanel = splitPane.getLeftComponent();
            splitPane.setDividerLocation(clipboardPanel.getPreferredSize().width);
        }
    }

    private boolean isClipboardVisible() {
        return splitPane.getDividerLocation() > 0;
    }
}
