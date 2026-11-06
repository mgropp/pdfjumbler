package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.jdragdroplist.JDragDropList;
import io.gropp.pdfjumbler.jdragdroplist.ListOperations;
import io.gropp.pdfjumbler.pdf.Page;

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
		if (ListOperations.move(list.getModel(), index, index - 1)) {
			list.setSelectedIndex(index - 1);
		}
	}
}
