package io.gropp.pdfjumbler.jdragdroplist;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.undo.UndoManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListOperationsTest {
	private UndoableListModel<String> model;
	private UndoManager undoManager;

	@BeforeEach
	void setUp() {
		model = new UndoableListModel<>();
		for (String item : new String[] {"a", "b", "c", "d"}) {
			model.add(model.getSize(), item);
		}
		undoManager = new UndoManager();
		model.addUndoableEditListener(undoManager);
	}

	@Test
	void movesItemUp() {
		assertThat(ListOperations.move(model, 2, 1)).isTrue();

		assertThat(model.getList()).containsExactly("a", "c", "b", "d");
	}

	@Test
	void movesItemDown() {
		assertThat(ListOperations.move(model, 0, 1)).isTrue();

		assertThat(model.getList()).containsExactly("b", "a", "c", "d");
	}

	@Test
	void doesNotMoveBeyondListBounds() {
		assertThat(ListOperations.move(model, 0, -1)).isFalse();
		assertThat(ListOperations.move(model, 3, 4)).isFalse();
		assertThat(ListOperations.move(model, -1, 0)).isFalse();

		assertThat(model.getList()).containsExactly("a", "b", "c", "d");
		assertThat(undoManager.canUndo()).isFalse();
	}

	@Test
	void removesGivenIndicesInAnyOrder() {
		ListOperations.removeAll(model, new int[] {3, 0, 2});

		assertThat(model.getList()).containsExactly("b");
	}

	@Test
	void removeAllDoesNotReorderCallerIndices() {
		int[] indices = {3, 0};

		ListOperations.removeAll(model, indices);

		assertThat(indices).containsExactly(3, 0);
	}
}
