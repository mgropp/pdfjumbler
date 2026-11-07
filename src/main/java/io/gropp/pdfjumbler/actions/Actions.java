package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.config.UserPreferences;
import io.gropp.pdfjumbler.pdf.PdfProcessingFactory;
import io.gropp.pdfjumbler.ui.Dialogs;
import io.gropp.pdfjumbler.ui.Icons;
import io.gropp.pdfjumbler.ui.pagelist.PdfList;
import javax.swing.*;
import javax.swing.undo.UndoManager;

/**
 * Actions & workers for the MainFrame class.
 *
 * @author Martin Gropp
 */
public class Actions {
    private final Action docOpenAction;
    private final Action docSaveAction;
    private final Action docOpenAction2;
    private final Action docSaveAction2;
    private final Action undoAction;
    private final Action redoAction;
    private final Action zoomOutAction;
    private final Action zoomInAction;
    private final Action clearAction;
    private final Action delAction;
    private final Action rotateCwAction;
    private final Action rotateCcwAction;
    private final Action moveUpAction;
    private final Action moveDownAction;
    private final Action viewListAction;
    private final Action viewThumbnailsAction;

    public Actions(
            Dialogs dialogs,
            FileOperations fileOperations,
            PdfList mainList,
            PdfList secondaryList,
            UndoManager undoManager,
            UserPreferences userPreferences,
            PdfProcessingFactory processing) {
        docOpenAction = new DocOpenAction(dialogs, fileOperations, mainList);
        docSaveAction = new DocSaveAction(dialogs, fileOperations, mainList);
        docOpenAction2 = new DocOpenAction(dialogs, fileOperations, secondaryList, Icons.Size16.DOCUMENT_OPEN);
        docSaveAction2 = new DocSaveAction(dialogs, fileOperations, secondaryList, Icons.Size16.DOCUMENT_SAVE);
        undoAction = new UndoAction(undoManager);
        redoAction = new RedoAction(undoManager);
        zoomOutAction = new ZoomOutAction(mainList);
        zoomInAction = new ZoomInAction(mainList);
        clearAction = new ClearAction(dialogs, mainList);
        delAction = new DelAction(mainList);
        rotateCwAction = new RotateClockwiseAction(mainList, undoManager);
        rotateCcwAction = new RotateCounterClockwiseAction(mainList, undoManager);
        moveUpAction = new MoveUpAction();
        moveDownAction = new MoveDownAction();
        viewListAction = new ViewListAction(mainList, secondaryList, userPreferences);
        viewThumbnailsAction = new ViewThumbnailsAction(mainList, secondaryList, userPreferences);
    }

    public Action getUndoAction() {
        return undoAction;
    }

    public Action getRedoAction() {
        return redoAction;
    }

    public Action getRotateClockwiseAction() {
        return rotateCwAction;
    }

    public Action getRotateCounterClockwiseAction() {
        return rotateCcwAction;
    }

    public Action getClearAction() {
        return clearAction;
    }

    public Action getDeleteAction() {
        return delAction;
    }

    public Action getDocOpenAction() {
        return docOpenAction;
    }

    public Action getDocSaveAction() {
        return docSaveAction;
    }

    public Action getDocOpenAction2() {
        return docOpenAction2;
    }

    public Action getDocSaveAction2() {
        return docSaveAction2;
    }

    public Action getZoomOutAction() {
        return zoomOutAction;
    }

    public Action getZoomInAction() {
        return zoomInAction;
    }

    public Action getMoveUpAction() {
        return moveUpAction;
    }

    public Action getMoveDownAction() {
        return moveDownAction;
    }

    public Action getViewListAction() {
        return viewListAction;
    }

    public Action getViewThumbnailsAction() {
        return viewThumbnailsAction;
    }
}
