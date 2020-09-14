package net.sourceforge.pdfjumbler.jdragdroplist;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.undo.AbstractUndoableEdit;

class ListEdit<T> extends AbstractUndoableEdit {
	private static final long serialVersionUID = 1158687785913642971L;

	public static enum Type {
		ADD, ADD_MULTIPLE,
		REMOVE, REMOVE_MULTIPLE,
		SET
	}
	
	final Type type;
	final UndoableList<T> list;
	final UndoableList<T> list2;
	final int index;
	
	final T item;
	final T item2;
	final Collection<T> items;
	
	ListEdit(Type type, UndoableList<T> list, int index, T item) {
		this(type, list, index, item, null);
	}
	
	ListEdit(Type type, UndoableList<T> list, int index, T item, T item2) {
		if ((type != Type.ADD) && (type != Type.REMOVE) && (type != Type.SET)) {
			throw new IllegalArgumentException();
		}
		
		this.type = type;
		this.list = list;
		this.list2 = null;
		this.index = index;
		this.item = item;
		this.item2 = item2;
		this.items = null;
	}

	ListEdit(Type type, UndoableList<T> list, int index, Collection<? extends T> items) {
		if ((type != Type.ADD_MULTIPLE) && (type != Type.REMOVE_MULTIPLE)) {
			throw new IllegalArgumentException();
		}
		
		this.type = type;
		this.list = list;
		this.list2 = null;
		this.index = index;
		this.item = null;
		this.item2 = null;
		this.items = new ArrayList<>(items);
	}
	
	@Override
	public void undo() {
		list.undo(this);
	}
	
	@Override
	public void redo() {
		list.redo(this);
	}
	
	@Override
	public String toString() {
		if (items == null) {
			return
				type + " @ " + index + ": " + item +
				((item2 == null) ? "" : " -> " + item2);
		} else {
			return
				type + " @ " + index + ": " + items;
		}
	}

	@Override
	public String getPresentationName() {
		return toString();
	}
}
