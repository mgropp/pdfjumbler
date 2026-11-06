package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.jdragdroplist.JDragDropList;
import io.gropp.pdfjumbler.pdf.Page;

import javax.swing.*;
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
		parent.getUndoManager().addEdit(
			RotatePagesEdit.apply(list.getModel(), list.getSelectedIndices(), degrees)
		);
	}
}
