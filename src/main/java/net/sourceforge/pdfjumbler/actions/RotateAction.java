package net.sourceforge.pdfjumbler.actions;

import net.sourceforge.pdfjumbler.PdfJumbler;
import net.sourceforge.pdfjumbler.jdragdroplist.JDragDropList;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableListModel;
import net.sourceforge.pdfjumbler.pdf.Page;

import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import java.awt.event.ActionEvent;

public abstract class RotateAction extends AbstractAction {
	protected final PdfJumbler parent;
	private final int degrees;

	public RotateAction(String name, ImageIcon icon, PdfJumbler parent, int degrees) {
		super(name, icon);
		this.parent = parent;
		this.degrees = degrees;
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

		RotateEdit edit = new RotateEdit(model, selected, degrees);
		edit.redo();

		parent.getUndoManager().addEdit(edit);
	}

	private static class RotateEdit extends AbstractUndoableEdit {
		private final UndoableListModel<Page> listModel;
		private final int[] indices;
		private final int degrees;

		public RotateEdit(UndoableListModel<Page> listModel, int[] indices, int degrees) {
			this.listModel = listModel;
			this.indices = indices;
			this.degrees = degrees;
		}

		@Override
		public void undo() {
			for (int i : indices) {
				Page page = listModel.get(i);
				page.setRotation((page.getRotation() + 360 - degrees) % 360);
				listModel.fireContentsChanged(i, i);
			}
		}

		@Override
		public void redo() {
			for (int i : indices) {
				Page page = listModel.get(i);
				page.setRotation((page.getRotation() + degrees) % 360);
				listModel.fireContentsChanged(i, i);
			}
		}

		@Override
		public String toString() {
			return String.format("Rotate %s degrees", degrees);
		}

		@Override
		public String getPresentationName() {
			return toString();
		}
	}
}
