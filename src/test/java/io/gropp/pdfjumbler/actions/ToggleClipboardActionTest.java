package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.junit.jupiter.api.Test;

class ToggleClipboardActionTest {
    private final JPanel clipboardPanel = new JPanel();
    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, clipboardPanel, new JPanel());

    private ToggleClipboardActionTest() {
        clipboardPanel.setPreferredSize(new Dimension(150, 100));
        splitPane.setSize(800, 600);
        splitPane.setDividerLocation(0);
    }

    private static void perform(Action action) {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void expandsCollapsedClipboardAndMarksActionSelected() {
        Action action = new ToggleClipboardAction(splitPane);

        perform(action);

        assertThat(splitPane.getDividerLocation()).isEqualTo(150);
        assertThat(action.getValue(Action.SELECTED_KEY)).isEqualTo(true);
    }

    @Test
    void collapsesVisibleClipboardAndDeselectsAction() {
        Action action = new ToggleClipboardAction(splitPane);
        perform(action);

        perform(action);

        assertThat(splitPane.getDividerLocation()).isZero();
        assertThat(action.getValue(Action.SELECTED_KEY)).isEqualTo(false);
    }

    @Test
    void followsDividerMovedByUser() {
        Action action = new ToggleClipboardAction(splitPane);

        splitPane.setDividerLocation(200);

        assertThat(action.getValue(Action.SELECTED_KEY)).isEqualTo(true);
    }
}
