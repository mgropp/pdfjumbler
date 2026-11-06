package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import io.gropp.pdfjumbler.jdragdroplist.UndoableListModel;
import io.gropp.pdfjumbler.pdf.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RotatePagesEditTest {
	private final File file = new File("doc.pdf");
	private UndoableListModel<Page> model;

	@BeforeEach
	void setUp() {
		model = new UndoableListModel<>();
		for (int i = 0; i < 3; i++) {
			model.add(i, new Page(file, i));
		}
	}

	private List<Integer> rotations() {
		return model.getList().stream().map(Page::getRotation).toList();
	}

	@Test
	void rotatesSelectedPagesImmediately() {
		RotatePagesEdit.apply(model, new int[] {0, 2}, 90);

		assertThat(rotations()).containsExactly(90, 0, 90);
	}

	@Test
	void wrapsAroundAfterFullTurn() {
		model.get(0).setRotation(270);

		RotatePagesEdit.apply(model, new int[] {0}, 90);

		assertThat(rotations()).containsExactly(0, 0, 0);
	}

	@Test
	void counterClockwiseRotationIsExpressedAs270Degrees() {
		RotatePagesEdit.apply(model, new int[] {1}, 270);

		assertThat(rotations()).containsExactly(0, 270, 0);
	}

	@Test
	void undoAndRedoRestoreRotations() {
		RotatePagesEdit edit = RotatePagesEdit.apply(model, new int[] {1}, 90);

		edit.undo();
		assertThat(rotations()).containsExactly(0, 0, 0);

		edit.redo();
		assertThat(rotations()).containsExactly(0, 90, 0);
	}

	@Test
	void notifiesListenersAboutChangedPages() {
		List<String> events = new ArrayList<>();
		model.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {}

			@Override
			public void intervalRemoved(ListDataEvent e) {}

			@Override
			public void contentsChanged(ListDataEvent e) {
				events.add(e.getIndex0() + "-" + e.getIndex1());
			}
		});

		RotatePagesEdit.apply(model, new int[] {0, 2}, 90);

		assertThat(events).containsExactly("0-0", "2-2");
	}

	@Test
	void keepsOwnCopyOfIndices() {
		int[] indices = {0};
		RotatePagesEdit edit = RotatePagesEdit.apply(model, indices, 90);
		indices[0] = 2;

		edit.undo();

		assertThat(rotations()).containsExactly(0, 0, 0);
	}
}
