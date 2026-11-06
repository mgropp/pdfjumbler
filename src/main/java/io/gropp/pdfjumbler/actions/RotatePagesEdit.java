package io.gropp.pdfjumbler.actions;

import javax.swing.undo.AbstractUndoableEdit;

import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import io.gropp.pdfjumbler.pdf.Page;

final class RotatePagesEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = 1L;

	private final UndoableListModel<Page> listModel;
	private final int[] indices;
	private final int degrees;

	private RotatePagesEdit(UndoableListModel<Page> listModel, int[] indices, int degrees) {
		this.listModel = listModel;
		this.indices = indices.clone();
		this.degrees = degrees;
	}

	static RotatePagesEdit apply(UndoableListModel<Page> listModel, int[] indices, int degrees) {
		RotatePagesEdit edit = new RotatePagesEdit(listModel, indices, degrees);
		edit.rotateBy(degrees);
		return edit;
	}

	@Override
	public void undo() {
		super.undo();
		rotateBy(360 - degrees);
	}

	@Override
	public void redo() {
		super.redo();
		rotateBy(degrees);
	}

	private void rotateBy(int angle) {
		for (int i : indices) {
			Page page = listModel.get(i);
			page.setRotation((page.getRotation() + angle) % 360);
			listModel.fireContentsChanged(i, i);
		}
	}

	@Override
	public String getPresentationName() {
		return String.format("Rotate %s degrees", degrees);
	}
}
