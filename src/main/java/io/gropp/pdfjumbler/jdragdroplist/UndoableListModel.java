package io.gropp.pdfjumbler.jdragdroplist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;

public final class UndoableListModel<T> extends AbstractListModel<T> {
    private final UndoableList<T> list;

    public UndoableListModel(UndoableList<T> list) {
        this.list = list;
        ListDataListener listDataListener = new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                fireIntervalAdded(this, e.getIndex0(), e.getIndex1());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                fireIntervalRemoved(this, e.getIndex0(), e.getIndex1());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                fireContentsChanged(this, e.getIndex0(), e.getIndex1());
            }
        };
        list.addListDataListener(listDataListener);
    }

    public UndoableListModel() {
        this(new UndoableList<>(new ArrayList<>()));
    }

    public List<T> getList() {
        return Collections.unmodifiableList(list);
    }

    @Override
    public T getElementAt(int index) {
        return list.get(index);
    }

    @Override
    public int getSize() {
        return list.size();
    }

    public T get(int index) {
        return list.get(index);
    }

    public void add(int index, T item) {
        list.add(index, item);
    }

    public void clear() {
        list.clear();
    }

    public void remove(int index) {
        list.remove(index);
    }

    public void beginCompoundEdit(String name) {
        list.beginCompoundEdit(name);
    }

    public void beginCompoundEdit(CompoundEdit edit) {
        list.beginCompoundEdit(edit);
    }

    public void endCompoundEdit() {
        list.endCompoundEdit();
    }

    public void addUndoableEditListener(UndoableEditListener listener) {
        list.addUndoableEditListener(listener);
    }

    public void fireContentsChanged(int index0, int index1) {
        fireContentsChanged(this, index0, index1);
    }
}
