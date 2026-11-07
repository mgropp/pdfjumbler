package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.config.UserPreferences;
import io.gropp.pdfjumbler.testutil.InMemoryPreferences;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.junit.jupiter.api.Test;

class ViewActionsTest {
    private final PdfList mainList = TestPdfLists.pdfList(0);
    private final PdfList secondaryList = TestPdfLists.pdfList(0);
    private final UserPreferences preferences = new UserPreferences(new InMemoryPreferences());

    private static void perform(Action action) {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void thumbnailViewHidesTextInBothListsAndStoresPreference() {
        perform(new ViewThumbnailsAction(mainList, secondaryList, preferences));

        assertThat(mainList.getShowCellText()).isFalse();
        assertThat(secondaryList.getShowCellText()).isFalse();
        assertThat(preferences.isShowText()).isFalse();
    }

    @Test
    void listViewShowsTextInBothListsAndStoresPreference() {
        perform(new ViewThumbnailsAction(mainList, secondaryList, preferences));

        perform(new ViewListAction(mainList, secondaryList, preferences));

        assertThat(mainList.getShowCellText()).isTrue();
        assertThat(secondaryList.getShowCellText()).isTrue();
        assertThat(preferences.isShowText()).isTrue();
    }
}
