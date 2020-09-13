package net.sourceforge.pdfjumbler;

import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.jdragdroplist.JDragDropList;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableListModel;
import net.sourceforge.pdfjumbler.pdf.*;
import net.sourceforge.pdfjumbler.util.FileUtils;
import org.tinylog.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Actions & workers for the main PdfJumbler class.
 *
 * @author Martin Gropp
 */
class Actions {
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());

	public static class DocOpenAction extends AbstractAction {
		private static final long serialVersionUID = -1652454884847492822L;

		private static String lastOpenedFileLocation = null;
		private final PdfJumbler parent;
		private final PdfList list;

		public DocOpenAction(PdfJumbler parent, PdfList list) {
			this(parent, list, Icons.DOCUMENT_OPEN);
		}

		public DocOpenAction(PdfJumbler parent, PdfList list, Icon icon) {
			super(resources.getString(I18nKeys.OPEN_DOCUMENT), icon);
			this.parent = parent;
			this.list = list;

			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.OPEN_DOCUMENT));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_OPEN));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(DocOpenAction.lastOpenedFileLocation);
			chooser.setMultiSelectionEnabled(true);
			chooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File file) {
					DocOpenAction.lastOpenedFileLocation = file.getParent();
					return
						file.isDirectory() ||
						(file.exists() && file.isFile() && file.canRead() && file.getName().toLowerCase().endsWith(".pdf"));
				}

				@Override
				public String getDescription() {
					return resources.getString(I18nKeys.FILE_FILTER_PDF);
				}

			});

			if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				PdfJumbler.openFiles(list, -1, Arrays.asList(chooser.getSelectedFiles()));
			}
		}
	}

	public static class DocSaveAction extends AbstractAction {
		private static final long serialVersionUID = -1652454884847492822L;
	    private static String lastSavedLocation = null;
		private final PdfJumbler parent;
		private final PdfList list;

		public DocSaveAction(PdfJumbler parent, PdfList list) {
			this(parent, list, Icons.DOCUMENT_SAVE);
		}

		public DocSaveAction(PdfJumbler parent, PdfList list, Icon icon) {
			super(resources.getString(I18nKeys.SAVE_DOCUMENT), icon);
			this.parent = parent;
			this.list = list;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.SAVE_DOCUMENT));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_SAVE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(DocSaveAction.lastSavedLocation);
			chooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return
						file.isDirectory() ||
						((file.getParentFile() != null) && file.getParentFile().exists()) &&
						(
							(!file.exists() ||
							file.isFile()) && file.canWrite() && file.getName().toLowerCase().endsWith(".pdf")
						);
				}

				@Override
				public String getDescription() {
					return resources.getString(I18nKeys.FILE_FILTER_PDF);
				}
			});

			if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (!file.getName().toLowerCase().endsWith(".pdf")) {
					file = new File(file.getAbsolutePath() + ".pdf");
				}
				if (
					file.exists() &&
					(JOptionPane.showConfirmDialog(
						parent,
						String.format(resources.getString(I18nKeys.OVERWRITE_FILE_TEXT), file.getAbsolutePath()),
						resources.getString(I18nKeys.OVERWRITE_FILE_TITLE),
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE
					) != JOptionPane.OK_OPTION)
				) {
					return;
				}
				DocSaveAction.lastSavedLocation = file.getParent();
				PdfJumbler.saveFile(list, file);
			}
		}
	}

	public static class ZoomOutAction extends AbstractAction {
		private static final long serialVersionUID = -8473933777713981409L;
		private final PdfJumbler parent;

		public ZoomOutAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.ZOOM_OUT), Icons.ZOOM_OUT);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.ZOOM_OUT));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_ZOOM_OUT));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			parent.mainList.setThumbnailSize(parent.mainList.getThumbnailSize() - 10);
		}
	}

	public static class ZoomInAction extends AbstractAction {
		private static final long serialVersionUID = 7357355236947950441L;
		private final PdfJumbler parent;

		public ZoomInAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.ZOOM_IN), Icons.ZOOM_IN);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.ZOOM_IN));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_ZOOM_IN));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			parent.mainList.setThumbnailSize(parent.mainList.getThumbnailSize() + 10);
		}
	}

	public static class ClearAction extends AbstractAction {
		private static final long serialVersionUID = 8258949195959150573L;
		private final PdfJumbler parent;

		public ClearAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.CLEAR_LIST), null);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.CLEAR_LIST));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			UndoableListModel<Page> model = parent.mainList.getModel();
			if (
				(model.getSize() > 0) &&
				(JOptionPane.showConfirmDialog(
					parent,
					resources.getString(I18nKeys.CONFIRM_CLEAR_LIST_TEXT),
					resources.getString(I18nKeys.CONFIRM_CLEAR_LIST_TITLE),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE
			) == JOptionPane.OK_OPTION)
			) {
				model.clear();
			}
		}
	}

	public static class DelAction extends AbstractAction {
		private static final long serialVersionUID = 8258949195959150573L;
		private final PdfJumbler parent;

		public DelAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.DELETE), Icons.EDIT_DELETE);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.DELETE));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_DELETE));
		}

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			JDragDropList<Page> list;
			if (e.getSource() instanceof JDragDropList) {
				list = (JDragDropList<Page>)e.getSource();
			} else {
				list = parent.mainList;
			}
			UndoableListModel<Page> model = list.getModel();
			int[] selected = list.getSelectedIndices();
			Arrays.sort(selected);
			for (int i = selected.length-1; i >= 0; i--) {
				model.remove(selected[i]);
			}
		}
	}

	public static abstract class RotateAction extends AbstractAction {
		protected final PdfJumbler parent;
		private final int degrees;

		public RotateAction(String name, ImageIcon icon, PdfJumbler parent, int degrees) {
			super(name, icon);
			this.parent = parent;
			this.degrees = degrees;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			JDragDropList<Page> list;
			if (e.getSource() instanceof JDragDropList) {
				list = (JDragDropList<Page>)e.getSource();
			} else {
				list = parent.mainList;
			}
			UndoableListModel<Page> model = list.getModel();
			int[] selected = list.getSelectedIndices();

			RotateEdit edit = new RotateEdit(model, selected, degrees);
			edit.redo();

			parent.undoManager.addEdit(edit);
		}
	}

	public static class RotateEdit extends AbstractUndoableEdit {
		private final UndoableListModel<Page> listModel;
		private final int[] indices;
		private final int degrees;

		public RotateEdit(UndoableListModel<Page> listModel, int[] indices, int degrees) {
			this.listModel = listModel;
			this.indices = indices;
			this.degrees = degrees;
		}

		@Override
		public void undo() {
			for (int i : indices) {
				Page page = listModel.get(i);
				page.setRotation((page.getRotation() + 360 - degrees) % 360);
				listModel.fireContentsChanged(i, i);
			}
		}

		@Override
		public void redo() {
			for (int i : indices) {
				Page page = listModel.get(i);
				page.setRotation((page.getRotation() + degrees) % 360);
				listModel.fireContentsChanged(i, i);
			}
		}

		@Override
		public String toString() {
			return String.format("Rotate %s degrees", degrees);
		}
	}

	public static class RotateClockwiseAction extends RotateAction {
		private static final long serialVersionUID = 8258949195959150573L;

		public RotateClockwiseAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.ROTATE_CW), Icons.ROTATE_CW, parent, 90);
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.ROTATE_CW));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_ROTATE_CW));
		}
	}

	public static class RotateCounterClockwiseAction extends RotateAction {
		private static final long serialVersionUID = 8258949195959150573L;

		public RotateCounterClockwiseAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.ROTATE_CCW), Icons.ROTATE_CCW, parent, 270);
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.ROTATE_CCW));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_ROTATE_CCW));
		}
	}

	public static class AboutAction extends AbstractAction {
		private static final long serialVersionUID = -6505580153294146608L;
		private final PdfJumbler parent;

		public AboutAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.ABOUT));
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.ABOUT));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(
				parent,
				String.format(
					resources.getString(I18nKeys.ABOUT_TEXT),
					PdfJumbler.VERSION_STRING,
					PdfProcessingFactory.getEditor().getClass().getCanonicalName(),
					PdfProcessingFactory.getRenderer().getClass().getCanonicalName()
				),
				resources.getString(I18nKeys.ABOUT_TITLE),
				JOptionPane.INFORMATION_MESSAGE
			);
		}
	}

	public static class ChangeEditorAction extends AbstractAction {
		private static final long serialVersionUID = -8468488158161906330L;
		private final PdfJumbler parent;
		private final Class<? extends PdfEditor> editorClass;

		public ChangeEditorAction(PdfJumbler parent, Class<? extends PdfEditor> editorClass) {
			super(PdfProcessingFactory.getFriendlyName(editorClass));
			this.parent = parent;
			this.editorClass = editorClass;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				PdfProcessingFactory.setEditorClass(editorClass);
			}
			catch (Exception ex) {
				Logger.error(ex);
				JOptionPane.showMessageDialog(parent, ex.getLocalizedMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static class ChangeRendererAction extends AbstractAction {
		private static final long serialVersionUID = 8330129264136353423L;
		private final PdfJumbler parent;
		private final Class<? extends PdfRenderer> rendererClass;

		public ChangeRendererAction(PdfJumbler parent, Class<? extends PdfRenderer> rendererClass) {
			super(PdfProcessingFactory.getFriendlyName(rendererClass));
			this.parent = parent;
			this.rendererClass = rendererClass;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				PdfProcessingFactory.setRendererClass(rendererClass);
			}
			catch (Exception ex) {
				Logger.error(ex);
				JOptionPane.showMessageDialog(parent, ex.getLocalizedMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static class MenuAction extends AbstractAction {
		private static final long serialVersionUID = -3880011502734901446L;
		private final PdfJumbler parent;
		public JPopupMenu menu = new JPopupMenu();
		private Component component = null;

		public MenuAction(PdfJumbler parent) {
			super(resources.getString(I18nKeys.MENU), Icons.MENU);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.MENU));
			populate();
		}

		private void populate() {
			menu.add(parent.undoAction);
			menu.add(parent.redoAction);

			menu.addSeparator();

			menu.add(parent.rotateCwAction);
			menu.add(parent.rotateCcwAction);

			menu.addSeparator();

			menu.add(parent.delAction);
			menu.add(parent.clearAction);

			menu.addSeparator();

			ButtonGroup editorGroup = new ButtonGroup();
			JMenu editorMenu = new JMenu(resources.getString(I18nKeys.MENU_EDITOR));
			for (Class<? extends PdfEditor> cls : PdfProcessingFactory.getAvailableEditors()) {
				JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeEditorAction(parent, cls));
				editorGroup.add(item);
				editorMenu.add(item);
				if (PdfProcessingFactory.getEditorClass().equals(cls)) {
					item.setSelected(true);
				}
			}
			menu.add(editorMenu);

			ButtonGroup rendererGroup = new ButtonGroup();
			JMenu rendererMenu = new JMenu(resources.getString(I18nKeys.MENU_RENDERER));
			for (Class<? extends PdfRenderer> cls : PdfProcessingFactory.getAvailableRenderers()) {
				JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeRendererAction(parent, cls));
				rendererGroup.add(item);
				rendererMenu.add(item);
				if (PdfProcessingFactory.getRendererClass().equals(cls)) {
					item.setSelected(true);
				}
			}
			menu.add(rendererMenu);

			menu.addSeparator();
			menu.add(new AboutAction(parent));
		}

		public void setComponent(Component c) {
			this.component = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			menu.show(component, 0, component.getHeight());
		}
	}

	public static class MoveUpAction extends AbstractAction {
		private static final long serialVersionUID = 4204383549863556707L;

		public MoveUpAction() {
			super(resources.getString(I18nKeys.MOVE_UP));
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.MOVE_UP));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_MOVE_UP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!(e.getSource() instanceof JDragDropList<?>)) {
				return;
			}

			@SuppressWarnings({"unchecked", "rawtypes"})
			JDragDropList<Page> list = (JDragDropList)e.getSource();
			int index = list.getSelectedIndex();
			if (index <= 0) {
				return;
			}

			UndoableListModel<Page> model = list.getModel();
			Page page = model.get(index);
			model.remove(index);
			model.add(index-1, page);

			list.setSelectedIndex(index-1);
		}
	}

	public static class MoveDownAction extends AbstractAction {
		private static final long serialVersionUID = -4468213711264218766L;

		public MoveDownAction() {
			super(resources.getString(I18nKeys.MOVE_DOWN));
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.MOVE_DOWN));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_MOVE_DOWN));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!(e.getSource() instanceof JDragDropList<?>)) {
				return;
			}

			@SuppressWarnings({"unchecked", "rawtypes"})
			JDragDropList<Page> list = (JDragDropList)e.getSource();
			UndoableListModel<Page> model = list.getModel();

			int index = list.getSelectedIndex();
			if ((index < 0) || (index >= model.getSize()-1)) {
				return;
			}

			Page page = model.get(index);
			model.remove(index);
			model.add(index+1, page);

			list.setSelectedIndex(index+1);
		}
	}

	public static class ReloadingProcessorListener implements PdfProcessorListener {
		private final PdfJumbler parent;

		public ReloadingProcessorListener(PdfJumbler parent) {
			this.parent = parent;
		}

		@Override
		public void pdfRendererChanged(PdfRenderer oldRenderer, PdfRenderer newRenderer) {
			try {
				oldRenderer.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			finally {
				parent.mainList.updateUI();
				parent.secondaryList.updateUI();
			}
		}

		@Override
		public void pdfEditorChanged(final PdfEditor oldEditor, final PdfEditor newEditor) {
			try {
				oldEditor.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class OpenDocumentWorker extends SwingWorker<Void, java.util.List<Page>> {
		private final UndoableListModel<Page> model;
		private final AtomicInteger insertPos;
		private final Collection<File> files;

		public OpenDocumentWorker(UndoableListModel<Page> model, int insertPos, Collection<File> files) {
			this.model = model;
			this.insertPos = new AtomicInteger((insertPos < 0) ? model.getSize() : insertPos);
			this.files = files;

			// execute is final :/
			model.beginCompoundEdit("Open");
		}

		@Override
		protected Void doInBackground() throws Exception {
			setProgress(0);

			int nFiles = 0;
			for (File file : files) {
				Logger.info("Opening file: {}", file);
				firePropertyChange("note", "", file.getName());

				publish(DocumentManager.getPages(file));

				nFiles++;
				setProgress((int)(100 * (nFiles / (double)files.size())));
			}

			return null;
		}

		@Override
		protected void process(java.util.List<java.util.List<Page>> pages) {
			for (java.util.List<Page> pageList : pages) {
				for (Page page : pageList) {
					model.add(
						insertPos.getAndIncrement(),
						page
					);
				}
			}
		}

		@Override
		protected void done() {
			model.endCompoundEdit();
		}
	}

	public static class SaveDocumentWorker extends SwingWorker<Void,Void> {
		private final List<Page> pages;
		private final File file;

		public SaveDocumentWorker(UndoableListModel<Page> model, File file) {
			this.pages = model.getList();
			this.file = file;
		}

		@Override
		protected Void doInBackground() throws Exception {
			firePropertyChange("note", "", file.getName());

			// TODO: Detect links! (java.nio.file?)

			if (file.exists() && DocumentManager.getAllFiles().contains(file)) {
				File tempFile = File.createTempFile("pdfjumbler", ".pdf");
				FileUtils.moveFile(file, tempFile);
				tempFile.deleteOnExit();

				for (Page page : DocumentManager.getPages(file)) {
					page.setFile(tempFile);
				}
			}

			PdfProcessingFactory.getEditor().saveDocument(
				pages,
				file
			);

			return null;
		}
	}

	public static class UndoAction extends AbstractAction {
		private static final long serialVersionUID = 4824090977507378704L;
		private final UndoManager undoManager;

		public UndoAction(UndoManager undoManager) {
			super(resources.getString(I18nKeys.UNDO));
			this.undoManager = undoManager;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.UNDO));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_UNDO));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Logger.debug("Can undo: {}", undoManager.canUndo());
			if (undoManager.canUndo()) {
				Logger.debug("Undoing: {}", undoManager.getPresentationName());
				undoManager.undo();
				Logger.debug("Next undo event: {}", undoManager.getPresentationName());
			}
		}
	}

	public static class RedoAction extends AbstractAction {
		private static final long serialVersionUID = -4360624396939210557L;
		private final UndoManager undoManager;

		public RedoAction(UndoManager undoManager) {
			super(resources.getString(I18nKeys.REDO));
			this.undoManager = undoManager;
			putValue(Action.SHORT_DESCRIPTION, resources.getString(I18nKeys.REDO));
			putValue(Action.ACCELERATOR_KEY, resources.getObject(I18nKeys.ACCELERATOR_REDO));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}
	}
}
