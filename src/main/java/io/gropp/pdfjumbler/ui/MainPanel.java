package io.gropp.pdfjumbler.ui;

import com.formdev.flatlaf.util.UIScale;
import io.gropp.pdfjumbler.actions.Actions;
import io.gropp.pdfjumbler.actions.MenuAction;
import io.gropp.pdfjumbler.actions.ToggleClipboardAction;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.io.Serial;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

class MainPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 6203945178412880345L;

    private static final int MAIN_MINIMUM_SIZE = 100;

    MainPanel(
            Component parent,
            Dialogs dialogs,
            Actions actions,
            PdfList mainList,
            PdfList secondaryList,
            PdfProcessingFactory processing) {
        super(new BorderLayout());
        mainList.setMinimumSize(mainMinimumSize());
        secondaryList.setMinimumSize(collapsedSize());

        JSplitPane splitPane = createSplitPane(actions, mainList, secondaryList);
        Action toggleClipboardAction = new ToggleClipboardAction(splitPane);
        add(
                createMainToolbar(parent, dialogs, actions, toggleClipboardAction, mainList, processing),
                BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private static JSplitPane createSplitPane(Actions actions, PdfList mainList, PdfList secondaryList) {
        var mainScrollPane = createMainScrollPane(mainList);
        var secondaryPanel = createSecondaryPanel(actions, secondaryList);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, secondaryPanel, mainScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(UIScale.scale(splitPane.getDividerSize()));
        splitPane.setDividerLocation(0.0);
        return splitPane;
    }

    private static JToolBar createMainToolbar(
            Component parent,
            Dialogs dialogs,
            Actions actions,
            Action toggleClipboardAction,
            PdfList mainList,
            PdfProcessingFactory processing) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(actions.getDocOpenAction());
        toolBar.add(actions.getDocSaveAction());
        toolBar.addSeparator();
        toolBar.add(actions.getZoomOutAction());
        toolBar.add(actions.getZoomInAction());
        toolBar.addSeparator();
        JButton delButton = toolBar.add(actions.getDeleteAction());
        delButton.setDropTarget(new DropTarget(delButton, new TrashDropTargetListener()));
        toolBar.addSeparator();
        toolBar.add(actions.getRotateClockwiseAction());
        toolBar.addSeparator();
        JToggleButton clipboardButton = new JToggleButton(toggleClipboardAction);
        clipboardButton.setHideActionText(true);
        toolBar.add(clipboardButton);

        toolBar.add(Box.createHorizontalGlue());
        MenuAction menuAction = new MenuAction(parent, dialogs, actions, mainList, processing);
        menuAction.setComponent(toolBar.add(menuAction));

        return toolBar;
    }

    private static JToolBar createSecondaryToolbar(Actions actions) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(actions.getDocOpenAction2());
        toolBar.add(actions.getDocSaveAction2());
        return toolBar;
    }

    private static JScrollPane createMainScrollPane(PdfList mainList) {
        JScrollPane mainScrollPane = new JScrollPane(mainList);
        mainScrollPane.setMinimumSize(mainMinimumSize());
        return mainScrollPane;
    }

    private static JPanel createSecondaryPanel(Actions actions, PdfList secondaryList) {
        var toolBar = createSecondaryToolbar(actions);

        JScrollPane secondaryScrollPane = new JScrollPane(secondaryList);

        JPanel secondaryPanel = new JPanel();
        secondaryPanel.setLayout(new BorderLayout());
        secondaryPanel.add(secondaryScrollPane, BorderLayout.CENTER);
        secondaryPanel.add(toolBar, BorderLayout.SOUTH);

        secondaryScrollPane.setMinimumSize(collapsedSize());
        secondaryPanel.setMinimumSize(collapsedSize());

        return secondaryPanel;
    }

    private static Dimension mainMinimumSize() {
        return new Dimension(UIScale.scale(MAIN_MINIMUM_SIZE), UIScale.scale(MAIN_MINIMUM_SIZE));
    }

    private static Dimension collapsedSize() {
        return new Dimension(0, 0);
    }
}
