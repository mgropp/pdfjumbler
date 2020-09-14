package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.Icons;
import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.jdragdroplist.JDragDropList;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableListModel;
import net.sourceforge.pdfjumbler.pdf.Page;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class DelAction extends AbstractAction {
	private static final long serialVersionUID = 8258949195959150573L;
	private final PdfJumbler parent;

	public DelAction(PdfJumbler parent) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.DELETE), Icons.EDIT_DELETE);
		this.parent = parent;
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.DELETE));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_DELETE));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		JDragDropList<Page> list;
		if (e.getSource() instanceof JDragDropList) {
			list = (JDragDropList<Page>)e.getSource();
		} else {
			list = parent.getMainPdfList();
		}
		UndoableListModel<Page> model = list.getModel();
		int[] selected = list.getSelectedIndices();
		Arrays.sort(selected);
		for (int i = selected.length-1; i >= 0; i--) {
			model.remove(selected[i]);
		}
	}
}
