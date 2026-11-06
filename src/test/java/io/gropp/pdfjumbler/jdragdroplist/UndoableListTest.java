package io.gropp.pdfjumbler.jdragdroplist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.undo.UndoManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UndoableListTest {
	private UndoableList<String> list;
	private UndoManager undoManager;
	private RecordingListDataListener dataListener;

	@BeforeEach
	void setUp() {
		list = new UndoableList<>(new ArrayList<>());
		undoManager = new UndoManager();
		dataListener = new RecordingListDataListener();
		list.addUndoableEditListener(undoManager);
	}

	private void fill(String... items) {
		for (String item : items) {
			list.add(item);
		}
		undoManager.discardAllEdits();
	}

	private void listenToData() {
		list.addListDataListener(dataListener);
	}

	@Test
	void addAppendsAndUndoRemoves() {
		list.add("a");
		list.add("b");

		assertThat(list).containsExactly("a", "b");

		undoManager.undo();
		assertThat(list).containsExactly("a");

		undoManager.redo();
		assertThat(list).containsExactly("a", "b");
	}

	@Test
	void addAtIndexInsertsAndUndoRemoves() {
		fill("a", "c");

		list.add(1, "b");
		assertThat(list).containsExactly("a", "b", "c");

		undoManager.undo();
		assertThat(list).containsExactly("a", "c");
	}

	@Test
	void removeByIndexAndUndoRestores() {
		fill("a", "b", "c");

		assertThat(list.remove(1)).isEqualTo("b");
		assertThat(list).containsExactly("a", "c");

		undoManager.undo();
		assertThat(list).containsExactly("a", "b", "c");

		undoManager.redo();
		assertThat(list).containsExactly("a", "c");
	}

	@Test
	void removeByObject() {
		fill("a", "b");

		assertThat(list.remove("b")).isTrue();
		assertThat(list.remove("x")).isFalse();
		assertThat(list).containsExactly("a");
	}

	@Test
	void setReplacesAndUndoRestores() {
		fill("a", "b");

		assertThat(list.set(1, "x")).isEqualTo("b");
		assertThat(list).containsExactly("a", "x");

		undoManager.undo();
		assertThat(list).containsExactly("a", "b");

		undoManager.redo();
		assertThat(list).containsExactly("a", "x");
	}

	@Test
	void addAllAndUndoRemovesAll() {
		fill("a", "d");

		list.addAll(1, List.of("b", "c"));
		assertThat(list).containsExactly("a", "b", "c", "d");

		undoManager.undo();
		assertThat(list).containsExactly("a", "d");

		undoManager.redo();
		assertThat(list).containsExactly("a", "b", "c", "d");
	}

	@Test
	void clearAndUndoRestoresAll() {
		fill("a", "b", "c");

		list.clear();
		assertThat(list).isEmpty();

		undoManager.undo();
		assertThat(list).containsExactly("a", "b", "c");

		undoManager.redo();
		assertThat(list).isEmpty();
	}

	@Test
	void removeAllIsSingleUndoableEdit() {
		fill("a", "b", "c", "d");

		assertThat(list.removeAll(List.of("b", "d"))).isTrue();
		assertThat(list).containsExactly("a", "c");

		undoManager.undo();
		assertThat(list).containsExactly("a", "b", "c", "d");
		assertThat(undoManager.canUndo()).isFalse();
	}

	@Test
	void retainAllKeepsOnlyGivenItems() {
		fill("a", "b", "c");

		list.retainAll(List.of("b"));
		assertThat(list).containsExactly("b");

		undoManager.undo();
		assertThat(list).containsExactly("a", "b", "c");
	}

	@Test
	void compoundEditUndoesAllChangesAtOnce() {
		fill("a", "b", "c");

		list.beginCompoundEdit("Move");
		String item = list.remove(0);
		list.add(2, item);
		list.endCompoundEdit();

		assertThat(list).containsExactly("b", "c", "a");

		undoManager.undo();
		assertThat(list).containsExactly("a", "b", "c");
		assertThat(undoManager.canUndo()).isFalse();

		undoManager.redo();
		assertThat(list).containsExactly("b", "c", "a");
	}

	@Test
	void subListChangesAffectMainList() {
		fill("a", "b", "c", "d");

		List<String> subList = list.subList(1, 3);
		assertThat(subList).containsExactly("b", "c");

		subList.set(0, "x");
		subList.remove(1);

		assertThat(list).containsExactly("a", "x", "d");

		undoManager.undo();
		assertThat(list).containsExactly("a", "x", "c", "d");

		undoManager.undo();
		assertThat(list).containsExactly("a", "b", "c", "d");
	}

	@Test
	void iteratorDoesNotSupportModification() {
		fill("a");

		assertThatThrownBy(() -> list.iterator().remove()).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void listenersMustNotModifyList() {
		list.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {
				list.add("nested");
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {}

			@Override
			public void contentsChanged(ListDataEvent e) {}
		});

		assertThatThrownBy(() -> list.add("a")).isInstanceOf(ConcurrentModificationException.class);
	}

	@Test
	void equalsAndHashCodeCompareElements() {
		fill("a", "b");
		UndoableList<String> other = new UndoableList<>(new ArrayList<>(List.of("a", "b")));

		assertThat(list).isEqualTo(other);
		assertThat(list.hashCode()).isEqualTo(other.hashCode());
		assertThat(list).isNotEqualTo(new UndoableList<>(new ArrayList<>(List.of("a"))));
	}

	@Test
	void addFiresIntervalAdded() {
		fill("a", "b");
		listenToData();

		list.add(1, "x");

		assertThat(dataListener.events).containsExactly("added 1-1");
	}

	@Test
	void addAllFiresIntervalAdded() {
		fill("a");
		listenToData();

		list.addAll(List.of("b", "c"));

		assertThat(dataListener.events).containsExactly("added 1-2");
	}

	@Test
	void removeFiresIntervalRemoved() {
		fill("a", "b");
		listenToData();

		list.remove(1);

		assertThat(dataListener.events).containsExactly("removed 1-1");
	}

	@Test
	void setFiresContentsChanged() {
		fill("a", "b");
		listenToData();

		list.set(0, "x");

		assertThat(dataListener.events).containsExactly("changed 0-0");
	}

	@Test
	void clearFiresIntervalRemoved() {
		fill("a", "b", "c");
		listenToData();

		list.clear();

		assertThat(dataListener.events).containsExactly("removed 0-2");
	}

	@Test
	void undoAddAllFiresIntervalRemovedForAddedItems() {
		fill("a");
		list.addAll(List.of("b", "c"));
		listenToData();

		undoManager.undo();

		assertThat(dataListener.events).containsExactly("removed 1-2");
	}

	@Test
	void addToSubListFiresEventWithMainListIndices() {
		fill("a", "b", "c", "d");
		listenToData();

		list.subList(2, 4).add(1, "x");

		assertThat(list).containsExactly("a", "b", "c", "x", "d");
		assertThat(dataListener.events).containsExactly("added 3-3");
	}

	private static final class RecordingListDataListener implements ListDataListener {
		final List<String> events = new ArrayList<>();

		@Override
		public void intervalAdded(ListDataEvent e) {
			events.add("added " + e.getIndex0() + "-" + e.getIndex1());
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			events.add("removed " + e.getIndex0() + "-" + e.getIndex1());
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			events.add("changed " + e.getIndex0() + "-" + e.getIndex1());
		}
	}
}
