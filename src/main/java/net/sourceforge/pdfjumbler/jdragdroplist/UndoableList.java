package net.sourceforge.pdfjumbler.jdragdroplist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

public class UndoableList<T> implements List<T> {
	private final List<T> list;
	private final UndoableList<T> parent;
	private final int offset;
	private final List<UndoableEditListener> editListeners;
	private final List<ListDataListener> listDataListeners;

	/*
	 * This is not meant to be thread-safe, but to prevent
	 * listeners from modifying the list.
	 */
	private final boolean[] editProtection;
	
	private final Deque<CompoundEdit> compoundEdits = new LinkedList<CompoundEdit>();
	
	public UndoableList(List<T> list) {
		this.list = list;
		this.parent = null;
		this.offset = 0;
		this.editProtection = new boolean[]{ false };
		this.editListeners = new ArrayList<UndoableEditListener>();
		this.listDataListeners = new ArrayList<ListDataListener>();
	}
	
	private UndoableList(UndoableList<T> parent, List<T> subList, int offset) {
		this.list = subList;
		this.parent = parent;
		this.offset = offset;
		this.editProtection = parent.editProtection;
		this.editListeners = parent.editListeners;
		this.listDataListeners = parent.listDataListeners;
	}
	
	private UndoableList<T> getMainList() {
		return (parent == null) ? this : parent;
	}
	
	public void addUndoableEditListener(UndoableEditListener l) {
		editListeners.add(l);
	}
	
	public void removeUndoableEditListener(UndoableEditListener l) {
		editListeners.remove(l);
	}
	
	public void addListDataListener(ListDataListener l) {
		listDataListeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		listDataListeners.remove(l);
	}

	public void beginCompoundEdit(final String name) {
		CompoundEdit edit = new CompoundEdit() {
			private static final long serialVersionUID = 2894975385303438798L;
			
			@Override
			public String getPresentationName() {
				return name;
			}
		};
		compoundEdits.push(edit);
	}

	public void beginCompoundEdit(CompoundEdit edit) {
		compoundEdits.push(edit);
	}
	
	public void endCompoundEdit() {
		CompoundEdit edit = compoundEdits.pop();
		edit.end();
		addEdit(edit);
	}
	
	private void addEdit(UndoableEdit edit) {
		if (compoundEdits.isEmpty()) {
			UndoableEditEvent editEvent = new UndoableEditEvent(getMainList(), edit);
			for (UndoableEditListener l : editListeners) {
				l.undoableEditHappened(editEvent);
			}
		} else {
			compoundEdits.peek().addEdit(edit);
		}
	}
	
	private void callListenersAdd(ListEdit<T> edit, ListDataEvent dataEvent) {
		assert (!editProtection[0]);
		
		editProtection[0] = true;
		try {
			if (edit != null) {
				addEdit(edit);
			}
			
			if (dataEvent != null) {
				for (ListDataListener l : listDataListeners) {
					l.intervalAdded(dataEvent);
				}
			}
		}
		finally {
			editProtection[0] = false;
		}
	}
	
	private void callListenersRemove(ListEdit<T> edit, ListDataEvent dataEvent) {
		assert (!editProtection[0]);
		
		editProtection[0] = true;
		try {
			if (edit != null) {
				addEdit(edit);
			}
			
			for (ListDataListener l : listDataListeners) {
				l.intervalRemoved(dataEvent);
			}
		}
		finally {
			editProtection[0] = false;
		}
	}

	private void callListenersSet(ListEdit<T> edit, ListDataEvent dataEvent) {
		assert (!editProtection[0]);
		
		editProtection[0] = true;
		try {
			if (edit != null) {
				addEdit(edit);
			}
			
			if (dataEvent != null) {
				for (ListDataListener l : listDataListeners) {
					l.contentsChanged(dataEvent);
				}
			}
		}
		finally {
			editProtection[0] = false;
		}
	}

	@Override
	public boolean add(T element) {
		add(size(), element);
		return true;
	}

	@Override
	public void add(int index, T element) {
		if (editProtection[0]) {
			throw new ConcurrentModificationException();
		}

		list.add(index, element);
		callListenersAdd(
			new ListEdit<T>(ListEdit.Type.ADD, getMainList(), index + offset, element),
			new ListDataEvent(getMainList(), ListDataEvent.INTERVAL_ADDED, index + offset, index)
		);		
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return addAll(size(), c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		if (editProtection[0]) {
			throw new ConcurrentModificationException();
		}

		list.addAll(index, c);
		callListenersAdd(
			new ListEdit<T>(ListEdit.Type.ADD_MULTIPLE, getMainList(), index + offset, c),
			new ListDataEvent(getMainList(), ListDataEvent.INTERVAL_ADDED, index + offset, index + offset + c.size() - 1)
		);
		return true;
	}

	@Override
	public void clear() {
		if (editProtection[0]) {
			throw new ConcurrentModificationException();
		}

		Collection<T> copy = new ArrayList<T>(list);
		list.clear();
		callListenersRemove(
			new ListEdit<T>(ListEdit.Type.REMOVE_MULTIPLE, getMainList(), offset, copy),
			new ListDataEvent(getMainList(), ListDataEvent.INTERVAL_REMOVED, offset, offset + copy.size() - 1)
		);
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		final ListIterator<T> it = list.listIterator(index);
		
		return new ListIterator<T>() {
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public boolean hasPrevious() {
				return it.hasPrevious();
			}

			@Override
			public T next() {
				return it.next();
			}

			@Override
			public int nextIndex() {
				return it.nextIndex();
			}

			@Override
			public T previous() {
				return it.previous();
			}

			@Override
			public int previousIndex() {
				return it.previousIndex();
			}

			@Override
			public void add(T e) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(T e) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index >= 0) {
			remove(index);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public T remove(int index) {
		if (editProtection[0]) {
			throw new ConcurrentModificationException();
		}

		T item = list.remove(index);
		callListenersRemove(
			new ListEdit<T>(ListEdit.Type.REMOVE, getMainList(), index + offset, item),
			new ListDataEvent(getMainList(), ListDataEvent.INTERVAL_REMOVED, index + offset, index + offset)
		);
		
		return item;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		beginCompoundEdit("Remove");
		try {
			for (Object o : c) {
				changed |= remove(o);
			}
		}
		finally {
			endCompoundEdit();
		}
			
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		ArrayList<T> removeItems = new ArrayList<T>();
		for (T item : list) {
			if (!c.contains(item)) {
				removeItems.add(item);
			}
		}
		
		return removeAll(removeItems);
	}

	@Override
	public T set(int index, T element) {
		if (editProtection[0]) {
			throw new ConcurrentModificationException();
		}

		T oldElement = list.set(index, element);
		callListenersSet(
			new ListEdit<T>(ListEdit.Type.SET, getMainList(), index + offset, oldElement, element),
			new ListDataEvent(getMainList(), ListDataEvent.CONTENTS_CHANGED, index + offset, index + offset)
		);
		
		return oldElement;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new UndoableList<T>(getMainList(), list.subList(fromIndex, toIndex), fromIndex + offset);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <U> U[] toArray(U[] a) {
		return list.toArray(a);
	}
	
	private void undoAdd(ListEdit<T> edit) {
		assert (edit.list.list.get(edit.index) == edit.item);
		edit.list.list.remove(edit.index);
		callListenersRemove(
			null,
			new ListDataEvent(edit.list, ListDataEvent.INTERVAL_REMOVED, edit.index, edit.index)
		);
	}
	
	private void undoRemove(ListEdit<T> edit) {
		edit.list.list.add(edit.index, edit.item);
		callListenersAdd(
			null,
			new ListDataEvent(edit.list, ListDataEvent.INTERVAL_ADDED, edit.index, edit.index)
		);
	}
	
	private void undoAddMultiple(ListEdit<T> edit) {
		for (T item : edit.items) {
			assert (edit.list.list.get(edit.index) == item);
			edit.list.list.remove(edit.index);
		}
		callListenersRemove(
			null,
			new ListDataEvent(edit.list, ListDataEvent.INTERVAL_REMOVED, edit.index, edit.index + edit.items.size() - 1)
		);
	}
	
	private void undoRemoveMultiple(ListEdit<T> edit) {
		int index = edit.index;
		for (T item : edit.items) {
			edit.list.list.add(index++, item);
		}
		callListenersAdd(
			null,
			new ListDataEvent(edit.list, ListDataEvent.INTERVAL_ADDED, edit.index, edit.index + edit.items.size())
		);
	}
	
	private void undoSet(ListEdit<T> edit) {
		edit.list.list.set(edit.index, edit.item);
		callListenersSet(
			null,
			new ListDataEvent(edit.list, ListDataEvent.CONTENTS_CHANGED, edit.index, edit.index)
		);
	}
	
	private void redoSet(ListEdit<T> edit) {
		edit.list.list.set(edit.index, edit.item2);
		callListenersSet(
			null,
			new ListDataEvent(edit.list, ListDataEvent.CONTENTS_CHANGED, edit.index, edit.index)
		);
	}
	
	void undo(ListEdit<T> edit) {
		if (editProtection[0]) {
			throw new ConcurrentModificationException();
		}
		if (edit.list != getMainList()) {
			throw new IllegalArgumentException();
		}

		switch (edit.type) {
		case ADD:
			undoAdd(edit);
			break;
		case REMOVE:
			undoRemove(edit);
			break;
		case ADD_MULTIPLE:
			undoAddMultiple(edit);
			break;
		case REMOVE_MULTIPLE:
			undoRemoveMultiple(edit);
			break;
		case SET:
			undoSet(edit);
			break;
		default:
			throw new AssertionError();
		}
	}
	
	void redo(ListEdit<T> edit) {
		if (editProtection[0]) {
			throw new ConcurrentModificationException();
		}
		
		switch (edit.type) {
		case ADD:
			undoRemove(edit);
			break;
		case REMOVE:
			undoAdd(edit);
			break;
		case ADD_MULTIPLE:
			undoRemoveMultiple(edit);
			break;
		case REMOVE_MULTIPLE:
			undoAddMultiple(edit);
			break;
		case SET:
			redoSet(edit);
			break;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof UndoableList) {
			if (((UndoableList<?>)other).size() != size()) {
				return false;
			}
			
			Iterator<?> it1 = iterator();
			Iterator<?> it2 = ((UndoableList<?>)other).iterator();
			
			while (it1.hasNext()) {
				Object o1 = it1.next();
				Object o2 = it2.next();
				if (o1 == null) {
					if (o2 != null) {
						return false;
					}
				} else if (!o1.equals(o2)) {
					return false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (Object obj : this) {
			hashCode = 31*hashCode + (obj == null ? 0 : obj.hashCode());
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (T item : this) {
			sb.append(' ');
			sb.append(item.toString());
		}
		sb.append(" ]");
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		final LinkedList<UndoableEdit> edits = new LinkedList<UndoableEdit>();
		UndoableEditListener editListener = new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				System.out.println(e.getEdit());
				edits.addFirst(e.getEdit());
			}
		};
		
		UndoableList<Integer> list = new UndoableList<Integer>(new LinkedList<Integer>());
		list.addUndoableEditListener(editListener);
		for (int i = 1; i < 10; i++) {
			list.add(2*i);
		}
		System.out.println(list);
		list.add(6, 13);
		System.out.println(list);
		list.set(3, 7);
		System.out.println(list);
		
		List<Integer> subList = list.subList(3, 6);
		System.out.println(subList);
		subList.set(0, 8);
		System.out.println(list);
		System.out.println(subList);
		subList.remove(1);
		
		System.out.println("----------------------------------");
		for (UndoableEdit edit : edits) {
			System.out.println("Undo " + edit);
			edit.undo();
			System.out.println(list);
		}
	}
}
