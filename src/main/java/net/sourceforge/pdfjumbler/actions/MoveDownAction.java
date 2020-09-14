package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.jdragdroplist.JDragDropList;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableListModel;
import net.sourceforge.pdfjumbler.pdf.Page;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MoveDownAction extends AbstractAction {
	private static final long serialVersionUID = -4468213711264218766L;

	public MoveDownAction() {
		super(PdfJumblerResources.getResources().getString(I18nKeys.MOVE_DOWN));
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.MOVE_DOWN));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_MOVE_DOWN));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!(e.getSource() instanceof JDragDropList<?>)) {
			return;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		JDragDropList<Page> list = (JDragDropList)e.getSource();
		UndoableListModel<Page> model = list.getModel();

		int index = list.getSelectedIndex();
		if ((index < 0) || (index >= model.getSize()-1)) {
			return;
		}

		Page page = model.get(index);
		model.remove(index);
		model.add(index+1, page);

		list.setSelectedIndex(index+1);
	}
}
