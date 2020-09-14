package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.jdragdroplist.JDragDropList;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableListModel;
import net.sourceforge.pdfjumbler.pdf.Page;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MoveUpAction extends AbstractAction {
	private static final long serialVersionUID = 4204383549863556707L;

	public MoveUpAction() {
		super(PdfJumblerResources.getResources().getString(I18nKeys.MOVE_UP));
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.MOVE_UP));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_MOVE_UP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!(e.getSource() instanceof JDragDropList<?>)) {
			return;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		JDragDropList<Page> list = (JDragDropList)e.getSource();
		int index = list.getSelectedIndex();
		if (index <= 0) {
			return;
		}

		UndoableListModel<Page> model = list.getModel();
		Page page = model.get(index);
		model.remove(index);
		model.add(index-1, page);

		list.setSelectedIndex(index-1);
	}
}
