package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.config.UserPreferences;
import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.testutil.InMemoryPreferences;
import io.gropp.pdfjumbler.testutil.RecordingDialogs;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.util.List;
import javax.swing.Action;
import javax.swing.undo.UndoManager;
import org.junit.jupiter.api.Test;

class ActionsTest {
    private final PdfList mainList = TestPdfLists.pdfList(0);
    private final PdfList secondaryList = TestPdfLists.pdfList(0);

    private Actions actions() {
        FileOperations fileOperations = new FileOperations(
                new RecordingDialogs(),
                worker -> true,
                new DocumentManager(file -> 0, () -> {}),
                TestPdfLists.processing());
        return new Actions(
                new RecordingDialogs(),
                fileOperations,
                mainList,
                secondaryList,
                new UndoManager(),
                new UserPreferences(new InMemoryPreferences()),
                TestPdfLists.processing());
    }

    @Test
    void everyActionIsCreatedWithAName() {
        Actions actions = actions();

        List<Action> all = List.of(
                actions.getUndoAction(),
                actions.getRedoAction(),
                actions.getRotateClockwiseAction(),
                actions.getRotateCounterClockwiseAction(),
                actions.getClearAction(),
                actions.getDeleteAction(),
                actions.getDocOpenAction(),
                actions.getDocSaveAction(),
                actions.getDocOpenAction2(),
                actions.getDocSaveAction2(),
                actions.getZoomOutAction(),
                actions.getZoomInAction(),
                actions.getMoveUpAction(),
                actions.getMoveDownAction(),
                actions.getViewListAction(),
                actions.getViewThumbnailsAction());

        assertThat(all)
                .allSatisfy(action ->
                        assertThat(action.getValue(Action.NAME)).asString().isNotBlank());
    }

    @Test
    void secondaryOpenAndSaveActionsAreDistinctFromMainOnes() {
        Actions actions = actions();

        assertThat(actions.getDocOpenAction2()).isNotSameAs(actions.getDocOpenAction());
        assertThat(actions.getDocSaveAction2()).isNotSameAs(actions.getDocSaveAction());
    }
}
