package io.gropp.pdfjumbler.jdragdroplist;

import java.util.Arrays;

public final class ListOperations {
    private ListOperations() {}

    public static <T> boolean move(UndoableListModel<T> model, int from, int to) {
        if (!isValidIndex(model, from) || !isValidIndex(model, to) || from == to) {
            return false;
        }
        model.beginCompoundEdit("Move");
        try {
            T item = model.get(from);
            model.remove(from);
            model.add(to, item);
        } finally {
            model.endCompoundEdit();
        }
        return true;
    }

    public static <T> void removeAll(UndoableListModel<T> model, int[] indices) {
        if (indices.length == 0) {
            return;
        }
        int[] sorted = indices.clone();
        Arrays.sort(sorted);
        model.beginCompoundEdit("Delete");
        try {
            for (int i = sorted.length - 1; i >= 0; i--) {
                model.remove(sorted[i]);
            }
        } finally {
            model.endCompoundEdit();
        }
    }

    private static boolean isValidIndex(UndoableListModel<?> model, int index) {
        return index >= 0 && index < model.getSize();
    }
}
