package io.gropp.pdfjumbler.ui;

import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.actions.Actions;
import io.gropp.pdfjumbler.actions.FileOperations;
import io.gropp.pdfjumbler.actions.ReloadingProcessorListener;
import io.gropp.pdfjumbler.config.UserPreferences;
import io.gropp.pdfjumbler.document.DocumentManager;
import io.gropp.pdfjumbler.edit.UniqueUndoManager;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import java.awt.BorderLayout;
import java.io.File;
import java.io.Serial;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;
import org.tinylog.Logger;

public class MainFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = 4382647271800905977L;

    private static final int CLIPBOARD_THUMBNAIL_SIZE = 16;

    private final PdfList mainList;

    private final FileOperations fileOperations;
    private final Actions actions;

    public void openFiles(Collection<File> files) {
        fileOperations.openFiles(mainList, -1, files);
    }

    private void installExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> ErrorDialog.show(this, throwable));
    }

    private static void registerAccelerators(JComponent component, Action... actions) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = component.getActionMap();
        for (Action action : actions) {
            inputMap.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action);
            actionMap.put(action, action);
        }
    }

    public MainFrame(UserPreferences userPreferences, PdfProcessingFactory processing) {
        Logger.info("PdfJumbler is starting");

        installExceptionHandler();
        setTitle(PdfJumbler.class.getSimpleName());
        setIconImage(Icons.PDF_JUMBLER.getImage());

        Dialogs dialogs = new SwingDialogs(this);
        UndoManager undoManager = new UniqueUndoManager();
        fileOperations = new FileOperations(
                dialogs, worker -> ProgressDialog.run(worker, this), createDocumentManager(processing), processing);
        mainList = createMainPdfList(fileOperations, undoManager, userPreferences, processing);
        PdfList secondaryList = createSecondaryPdfList(fileOperations, undoManager, userPreferences, processing);
        actions =
                new Actions(dialogs, fileOperations, mainList, secondaryList, undoManager, userPreferences, processing);
        add(new MainPanel(this, dialogs, actions, mainList, secondaryList, processing), BorderLayout.CENTER);

        processing.addProcessorListener(new ReloadingProcessorListener(mainList, secondaryList));

        registerGlobalAccelerators();
        registerListAccelerators(mainList);
        registerListAccelerators(secondaryList);
    }

    private void registerGlobalAccelerators() {
        registerAccelerators(
                getRootPane(),
                actions.getDocOpenAction(),
                actions.getDocSaveAction(),
                actions.getZoomInAction(),
                actions.getZoomOutAction(),
                actions.getUndoAction(),
                actions.getRedoAction(),
                actions.getRotateClockwiseAction(),
                actions.getRotateCounterClockwiseAction());
    }

    private void registerListAccelerators(PdfList list) {
        registerAccelerators(list, actions.getMoveUpAction(), actions.getMoveDownAction(), actions.getDeleteAction());
    }

    private static DocumentManager createDocumentManager(PdfProcessingFactory processing) {
        return new DocumentManager(
                file -> processing.getRenderer().getNumberOfPages(file),
                () -> processing.getRenderer().close());
    }

    private static PdfList createMainPdfList(
            FileOperations fileOperations,
            UndoManager undoManager,
            UserPreferences userPreferences,
            PdfProcessingFactory processing) {
        var list = new PdfList(userPreferences.isShowText(), processing, fileOperations::openFiles);
        list.setDisplayMessage(PdfJumblerResources.getResources().getString(I18nKeys.LIST_DROP_PDFS_TO_EDIT));
        list.getModel().addUndoableEditListener(undoManager);
        return list;
    }

    private static PdfList createSecondaryPdfList(
            FileOperations fileOperations,
            UndoManager undoManager,
            UserPreferences userPreferences,
            PdfProcessingFactory processing) {
        var list = new PdfList(userPreferences.isShowText(), processing, fileOperations::openFiles);
        list.setDisplayMessage(PdfJumblerResources.getResources().getString(I18nKeys.LIST_CLIPBOARD_EMPTY));
        list.setThumbnailSize(CLIPBOARD_THUMBNAIL_SIZE);
        list.getModel().addUndoableEditListener(undoManager);
        return list;
    }
}
