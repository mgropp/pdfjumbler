package net.sourceforge.pdfjumbler.jdragdroplist;

import java.util.LinkedList;

import javax.swing.ListModel;

public final class StandardListModel<T> extends UndoableList<T> implements ListModel {
	public StandardListModel() {
		super(new LinkedList<T>());
	}
	
	@Override
	public Object getElementAt(int index) {
		return get(index);
	}

	@Override
	public int getSize() {
		return size();
	}
}
