package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import io.gropp.pdfjumbler.ui.Dialogs;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.*;
import org.jspecify.annotations.Nullable;

public class MenuAction extends AbstractAction {
    @Serial
    private static final long serialVersionUID = -3880011502734901446L;

    private final Component parent;
    private final Dialogs dialogs;
    private final PdfList mainList;
    private final PdfProcessingFactory processing;
    private final Actions actions;
    public JPopupMenu menu = new JPopupMenu();
    private @Nullable Component component;

    public MenuAction(
            Component parent, Dialogs dialogs, Actions actions, PdfList mainList, PdfProcessingFactory processing) {
        super(PdfJumblerResources.getResources().getString(I18nKeys.MENU), Icons.MENU);
        this.parent = parent;
        this.dialogs = dialogs;
        this.mainList = mainList;
        this.processing = processing;
        this.actions = actions;
        putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.MENU));
        populate();
    }

    private void populate() {
        menu.add(actions.getUndoAction());
        menu.add(actions.getRedoAction());

        menu.addSeparator();

        menu.add(actions.getRotateClockwiseAction());
        menu.add(actions.getRotateCounterClockwiseAction());

        menu.addSeparator();

        menu.add(actions.getDeleteAction());
        menu.add(actions.getClearAction());

        menu.addSeparator();

        ButtonGroup viewGroup = new ButtonGroup();
        JMenu viewMenu = new JMenu(PdfJumblerResources.getResources().getString(I18nKeys.MENU_VIEW));

        JRadioButtonMenuItem itemViewList = new JRadioButtonMenuItem(actions.getViewListAction());
        itemViewList.setSelected(mainList.getShowCellText());
        viewGroup.add(itemViewList);
        viewMenu.add(itemViewList);

        JRadioButtonMenuItem itemViewThumbnails = new JRadioButtonMenuItem(actions.getViewThumbnailsAction());
        itemViewThumbnails.setSelected(!mainList.getShowCellText());
        viewGroup.add(itemViewThumbnails);
        viewMenu.add(itemViewThumbnails);

        menu.add(viewMenu);

        menu.addSeparator();

        ButtonGroup editorGroup = new ButtonGroup();
        JMenu editorMenu = new JMenu(PdfJumblerResources.getResources().getString(I18nKeys.MENU_EDITOR));
        for (Class<? extends PdfEditor> cls : processing.getAvailableEditors()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeEditorAction(dialogs, processing, cls));
            editorGroup.add(item);
            editorMenu.add(item);
            if (processing.getEditorClass().equals(cls)) {
                item.setSelected(true);
            }
        }
        menu.add(editorMenu);

        ButtonGroup rendererGroup = new ButtonGroup();
        JMenu rendererMenu = new JMenu(PdfJumblerResources.getResources().getString(I18nKeys.MENU_RENDERER));
        for (Class<? extends PdfRenderer> cls : processing.getAvailableRenderers()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeRendererAction(dialogs, processing, cls));
            rendererGroup.add(item);
            rendererMenu.add(item);
            if (processing.getRendererClass().equals(cls)) {
                item.setSelected(true);
            }
        }
        menu.add(rendererMenu);

        menu.addSeparator();
        menu.add(new AboutAction(parent, processing));
    }

    public void setComponent(Component c) {
        this.component = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (component != null) {
            menu.show(component, 0, component.getHeight());
        }
    }
}
