package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.Icons;
import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.pdf.PdfEditor;
import net.sourceforge.pdfjumbler.pdf.PdfProcessingFactory;
import net.sourceforge.pdfjumbler.pdf.PdfRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuAction extends AbstractAction {
	private static final long serialVersionUID = -3880011502734901446L;
	private final PdfJumbler parent;
	public JPopupMenu menu = new JPopupMenu();
	private Component component = null;

	public MenuAction(PdfJumbler parent) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.MENU), Icons.MENU);
		this.parent = parent;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.MENU));
		populate();
	}

	private void populate() {
		menu.add(parent.getActions().getUndoAction());
		menu.add(parent.getActions().getRedoAction());

		menu.addSeparator();

		menu.add(parent.getActions().getRotateClockwiseAction());
		menu.add(parent.getActions().getRotateCounterClockwiseAction());

		menu.addSeparator();

		menu.add(parent.getActions().getDeleteAction());
		menu.add(parent.getActions().getClearAction());

		menu.addSeparator();

		ButtonGroup viewGroup = new ButtonGroup();
		JMenu viewMenu = new JMenu(PdfJumblerResources.getResources().getString(I18nKeys.MENU_VIEW));

		JRadioButtonMenuItem itemViewList = new JRadioButtonMenuItem(parent.getActions().getViewListAction());
		itemViewList.setSelected(parent.getMainPdfList().getShowCellText());
		viewGroup.add(itemViewList);
		viewMenu.add(itemViewList);

		JRadioButtonMenuItem itemViewThumbnails = new JRadioButtonMenuItem(parent.getActions().getViewThumbnailsAction());
		itemViewThumbnails.setSelected(!parent.getMainPdfList().getShowCellText());
		viewGroup.add(itemViewThumbnails);
		viewMenu.add(itemViewThumbnails);

		menu.add(viewMenu);

		menu.addSeparator();

		ButtonGroup editorGroup = new ButtonGroup();
		JMenu editorMenu = new JMenu(PdfJumblerResources.getResources().getString(I18nKeys.MENU_EDITOR));
		for (Class<? extends PdfEditor> cls : PdfProcessingFactory.getAvailableEditors()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeEditorAction(parent, cls));
			editorGroup.add(item);
			editorMenu.add(item);
			if (PdfProcessingFactory.getEditorClass().equals(cls)) {
				item.setSelected(true);
			}
		}
		menu.add(editorMenu);

		ButtonGroup rendererGroup = new ButtonGroup();
		JMenu rendererMenu = new JMenu(PdfJumblerResources.getResources().getString(I18nKeys.MENU_RENDERER));
		for (Class<? extends PdfRenderer> cls : PdfProcessingFactory.getAvailableRenderers()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeRendererAction(parent, cls));
			rendererGroup.add(item);
			rendererMenu.add(item);
			if (PdfProcessingFactory.getRendererClass().equals(cls)) {
				item.setSelected(true);
			}
		}
		menu.add(rendererMenu);

		menu.addSeparator();
		menu.add(new AboutAction(parent));
	}

	public void setComponent(Component c) {
		this.component = c;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		menu.show(component, 0, component.getHeight());
	}
}
