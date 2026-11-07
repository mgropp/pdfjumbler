package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import io.gropp.pdfjumbler.testutil.TestPdfLists;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReloadingProcessorListenerTest {
    private final CountingList mainList = new CountingList();
    private final CountingList secondaryList = new CountingList();
    private final ReloadingProcessorListener listener = new ReloadingProcessorListener(mainList, secondaryList);

    private static final class CountingList extends PdfList {
        private int updates;

        CountingList() {
            super(true, TestPdfLists.processing(), (list, position, files) -> {});
        }

        @Override
        public void updateUI() {
            updates++;
            super.updateUI();
        }

        int resetUpdates() {
            int count = updates;
            updates = 0;
            return count;
        }
    }

    private static final class RecordingEditor implements PdfEditor {
        private final boolean failOnClose;
        private boolean closed;

        RecordingEditor(boolean failOnClose) {
            this.failOnClose = failOnClose;
        }

        @Override
        public void saveDocument(List<Page> pages, File file) {}

        @Override
        public void close() throws IOException {
            closed = true;
            if (failOnClose) {
                throw new IOException("close failed");
            }
        }
    }

    private static final class RecordingRenderer implements PdfRenderer {
        private final boolean failOnClose;
        private boolean closed;

        RecordingRenderer(boolean failOnClose) {
            this.failOnClose = failOnClose;
        }

        @Override
        public Image renderPage(Page page, int maxWidth, int maxHeight) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getNumberOfPages(File file) {
            return 0;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (failOnClose) {
                throw new IOException("close failed");
            }
        }
    }

    @Test
    void rendererChangeClosesOldRendererAndRefreshesBothLists() {
        RecordingRenderer oldRenderer = new RecordingRenderer(false);
        mainList.resetUpdates();
        secondaryList.resetUpdates();

        listener.pdfRendererChanged(oldRenderer, new RecordingRenderer(false));

        assertThat(oldRenderer.closed).isTrue();
        assertThat(mainList.resetUpdates()).isEqualTo(1);
        assertThat(secondaryList.resetUpdates()).isEqualTo(1);
    }

    @Test
    void failingRendererCloseIsWrappedButListsAreStillRefreshed() {
        RecordingRenderer oldRenderer = new RecordingRenderer(true);
        mainList.resetUpdates();
        secondaryList.resetUpdates();

        assertThatThrownBy(() -> listener.pdfRendererChanged(oldRenderer, new RecordingRenderer(false)))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);

        assertThat(mainList.resetUpdates()).isEqualTo(1);
        assertThat(secondaryList.resetUpdates()).isEqualTo(1);
    }

    @Test
    void editorChangeClosesOldEditorWithoutRefreshingLists() {
        RecordingEditor oldEditor = new RecordingEditor(false);
        mainList.resetUpdates();
        secondaryList.resetUpdates();

        listener.pdfEditorChanged(oldEditor, new RecordingEditor(false));

        assertThat(oldEditor.closed).isTrue();
        assertThat(mainList.resetUpdates()).isZero();
        assertThat(secondaryList.resetUpdates()).isZero();
    }

    @Test
    void failingEditorCloseIsWrapped() {
        RecordingEditor oldEditor = new RecordingEditor(true);

        assertThatThrownBy(() -> listener.pdfEditorChanged(oldEditor, new RecordingEditor(false)))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
    }
}
