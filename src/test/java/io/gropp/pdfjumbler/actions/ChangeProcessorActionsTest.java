package io.gropp.pdfjumbler.actions;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.document.Page;
import io.gropp.pdfjumbler.pdf.NoPdfProcessorException;
import io.gropp.pdfjumbler.pdf.PdfEditor;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.pdf.PdfRenderer;
import io.gropp.pdfjumbler.testutil.InMemoryPreferences;
import io.gropp.pdfjumbler.testutil.RecordingDialogs;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import javax.swing.Action;
import org.junit.jupiter.api.Test;

class ChangeProcessorActionsTest {
    private final RecordingDialogs dialogs = new RecordingDialogs();
    private final PdfProcessingFactory processing = createProcessing();

    public static class EditorA implements PdfEditor {
        @Override
        public void saveDocument(List<Page> pages, File file) {}

        @Override
        public void close() {}
    }

    public static class EditorB extends EditorA {}

    public static class BrokenEditor extends EditorA {
        public BrokenEditor() {
            throw new IllegalStateException("broken editor");
        }
    }

    public static class RendererA implements PdfRenderer {
        @Override
        public Image renderPage(Page page, int maxWidth, int maxHeight) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getNumberOfPages(File file) {
            return 0;
        }

        @Override
        public void close() {}
    }

    public static class RendererB extends RendererA {}

    public static class BrokenRenderer extends RendererA {
        public BrokenRenderer() {
            throw new IllegalStateException("broken renderer");
        }
    }

    private static PdfProcessingFactory createProcessing() {
        try {
            return new PdfProcessingFactory(
                    List.of(EditorA.class, EditorB.class, BrokenEditor.class),
                    List.of(RendererA.class, RendererB.class, BrokenRenderer.class),
                    new InMemoryPreferences(),
                    null,
                    null);
        } catch (NoPdfProcessorException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void perform(Action action) {
        action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Test
    void changeEditorSwitchesEditor() {
        perform(new ChangeEditorAction(dialogs, processing, EditorB.class));

        assertThat(processing.getEditor()).isInstanceOf(EditorB.class);
        assertThat(dialogs.errors).isEmpty();
    }

    @Test
    void failingEditorIsReportedAndKeepsPreviousEditor() {
        perform(new ChangeEditorAction(dialogs, processing, BrokenEditor.class));

        assertThat(processing.getEditor()).isInstanceOf(EditorA.class);
        assertThat(dialogs.errors).hasSize(1);
    }

    @Test
    void changeRendererSwitchesRenderer() {
        perform(new ChangeRendererAction(dialogs, processing, RendererB.class));

        assertThat(processing.getRenderer()).isInstanceOf(RendererB.class);
        assertThat(dialogs.errors).isEmpty();
    }

    @Test
    void failingRendererIsReportedAndKeepsPreviousRenderer() {
        perform(new ChangeRendererAction(dialogs, processing, BrokenRenderer.class));

        assertThat(processing.getRenderer()).isInstanceOf(RendererA.class);
        assertThat(dialogs.errors).hasSize(1);
    }
}
