package net.sourceforge.pdfjumbler;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.jdragdroplist.JDragDropList;
import net.sourceforge.pdfjumbler.jdragdroplist.StandardListModel;
import net.sourceforge.pdfjumbler.jdragdroplist.UndoableList;
import net.sourceforge.pdfjumbler.pdf.Page;
import net.sourceforge.pdfjumbler.pdf.PdfEditor;
import net.sourceforge.pdfjumbler.pdf.PdfProcessingFactory;
import net.sourceforge.pdfjumbler.pdf.PdfProcessorListener;
import net.sourceforge.pdfjumbler.pdf.PdfRenderer;
import net.sourceforge.pdfjumbler.util.FileUtils;

/**
 * PdfJumbler main class.
 * 
 * @author Martin Gropp
 */
public class PdfJumbler extends JFrame {
	private static final long serialVersionUID = 4382647271800905977L;
	public static final int VERSION = 20170310;
	public static final String VERSION_STRING = "2017-03-10";
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());

	private static PdfJumbler instance = null; 

	final PdfList mainList;
	final PdfList secondaryList;
	//final JToolBar toolBar;
	final UndoManager undoManager = new UniqueUndoManager();
	
	private final Action docOpenAction;
	private final Action docSaveAction; 
	
	private final Action docOpenAction2;
	private final Action docSaveAction2;
	
	private final Action zoomOutAction = new Actions.ZoomOutAction(this); 
	private final Action zoomInAction = new Actions.ZoomInAction(this); 
	
	private final Action clearAction = new Actions.ClearAction(this);
	private final Action delAction = new Actions.DelAction(this);
	
	private final Action moveUpAction = new Actions.MoveUpAction();
	private final Action moveDownAction = new Actions.MoveDownAction();

	final Action undoAction;
	final Action redoAction;
	
	static void openFiles(PdfList list, int insertPos, Collection<File> files) {
		if (files.size() > 0) {
			try {
				ProgressDialog.run(
					new Actions.OpenDocumentWorker(
						list.getModel(),
						insertPos,
						files
					),
					PdfJumbler.instance
				);
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(instance, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	static void openFiles(PdfList list, int insertPos, String... fileNames) {
		ArrayList<File> files = new ArrayList<File>(fileNames.length);
		for (String fileName : fileNames) {
			files.add(new File(fileName));
		}
		openFiles(list, insertPos, files);
	}

	static void saveFile(PdfList list, File file) {
		try {
			ProgressDialog.run(
				new Actions.SaveDocumentWorker(
					list.getModel(),
					file
				),
				instance
			);
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(instance, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void setLookAndFeel() {
		String plaf = System.getProperty("pdfjumbler.lookandfeel", "");
		if (plaf.equals("?")) {
			for (LookAndFeelInfo l : UIManager.getInstalledLookAndFeels()) {
				System.out.println(l.getName() + ": " + l.getClassName());
			}
			plaf = "";
		}
		
		if (plaf.length() == 0) {
			plaf = Preferences.userNodeForPackage(PdfJumbler.class).get("lookandfeel", "");
		}

		if (plaf.length() > 0) {
			try {
				UIManager.setLookAndFeel(plaf);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception e) {
			}	
		}
	}

	private void installExceptionHandler() {
		while (true) {
			try {
				SwingUtilities.invokeAndWait(
					new Runnable() {
						@Override
						public void run() {
							Thread.setDefaultUncaughtExceptionHandler(
								new UncaughtExceptionHandler() {
									@Override
									public void uncaughtException(Thread t, Throwable e) {
										e.printStackTrace();
										JOptionPane.showMessageDialog(PdfJumbler.this, e.getLocalizedMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
									}
								}
						);
						}
					}
				);
			}
			catch (InterruptedException e) {
				continue;
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}

			break;
		}
	}

	private static void registerAccelerators(JComponent component, Action... actions) {
		InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = component.getActionMap();
		for (Action action : actions) {
			inputMap.put((KeyStroke)action.getValue(Action.ACCELERATOR_KEY), action);
			actionMap.put(action, action);
		}
	}

	private PdfJumbler(String[] files) throws IOException {
		PdfJumbler.instance = this;

		installExceptionHandler();
		setTitle(getClass().getSimpleName());
		setIconImage(Icons.PDFJUMBLER.getImage());
		setLookAndFeel();

		// Prepare toolbars
		JToolBar toolBar = new JToolBar();
		JToolBar toolBar2 = new JToolBar();
		toolBar.setFloatable(false);
		toolBar2.setFloatable(false);
		
		// Lists
		mainList = new PdfList();
		mainList.setDisplayMessage(resources.getString("LIST_DROP_PDFS_TO_EDIT"));
		JScrollPane mainPane = new JScrollPane(mainList);
		
		secondaryList = new PdfList();
		secondaryList.setDisplayMessage(resources.getString("LIST_CLIPBOARD_EMPTY"));
		secondaryList.setThumbnailSize(16);
		JScrollPane secondaryPane = new JScrollPane(secondaryList);
		
		JPanel secondaryPanel = new JPanel();
		secondaryPanel.setLayout(new BorderLayout());
		secondaryPanel.add(secondaryPane, BorderLayout.CENTER);
		secondaryPanel.add(toolBar2, BorderLayout.SOUTH);

		mainList.getModel().addUndoableEditListener(undoManager);
		secondaryList.getModel().addUndoableEditListener(undoManager);
		
		mainList.setMinimumSize(new Dimension(100, 100));
		mainPane.setMinimumSize(new Dimension(100, 100));
		secondaryList.setMinimumSize(new Dimension(0, 0));
		secondaryPane.setMinimumSize(new Dimension(0, 0));
		secondaryPanel.setMinimumSize(new Dimension(0, 0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, secondaryPanel, mainPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(0.0);
		add(splitPane, BorderLayout.CENTER);

		
		// Actions
		docOpenAction = new Actions.DocOpenAction(this, mainList);
		docSaveAction = new Actions.DocSaveAction(this, mainList); 
		
		docOpenAction2 = new Actions.DocOpenAction(this, secondaryList, Icons.Size16.DOCUMENT_OPEN);
		docSaveAction2 = new Actions.DocSaveAction(this, secondaryList, Icons.Size16.DOCUMENT_SAVE);
				
		// Toolbar
		toolBar.add(docOpenAction);
		toolBar.add(docSaveAction);
		toolBar.addSeparator();
		toolBar.add(zoomOutAction);
		toolBar.add(zoomInAction);
		toolBar.addSeparator();
		JButton delButton = toolBar.add(delAction);
		delButton.setDropTarget(new DropTarget(delButton, new TrashDropTargetListener()));
		toolBar.add(clearAction);

		undoAction = new Actions.UndoAction(undoManager);
		redoAction = new Actions.RedoAction(undoManager);
		
		toolBar.add(Box.createHorizontalGlue());
		Actions.WrenchAction wrenchAction = new Actions.WrenchAction(this);
		wrenchAction.setComponent(toolBar.add(wrenchAction));

		add(toolBar, BorderLayout.NORTH);

		// Toolbar 2
		toolBar2.add(Box.createHorizontalGlue());
		toolBar2.add(docOpenAction2);
		toolBar2.add(docSaveAction2);

		
		// Open files
		openFiles(mainList, -1, files);

		// Listen for editor/renderer changes
		PdfProcessingFactory.addProcessorListener(new Actions.ReloadingProcessorListener(this));

		// Register accelerators
		registerAccelerators(
			getRootPane(),
			docOpenAction, docSaveAction,
			zoomInAction, zoomOutAction,
			undoAction, redoAction
		);
		registerAccelerators(
			mainList,
			moveUpAction, moveDownAction, delAction
		);
		registerAccelerators(
			secondaryList,
			moveUpAction, moveDownAction, delAction
		);
	}

	public static void main(String[] args) {
		try {
			PdfJumbler frame = new PdfJumbler(args);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(320, 600);
			frame.setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		}
	}
}

/**
 * Actions & workers for the main PdfJumbler class.
 * Moved to an extra class for readability.
 * 
 * @author Martin Gropp <martin.gropp@googlemail.com>
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
			super(resources.getString("OPEN_DOCUMENT"), icon);
			this.parent = parent;
			this.list = list;

			putValue(Action.SHORT_DESCRIPTION, resources.getString("OPEN_DOCUMENT"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:OPEN"));
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
					return resources.getString("FILE_FILTER_PDF");
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
			super(resources.getString("SAVE_DOCUMENT"), icon);
			this.parent = parent;
			this.list = list;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("SAVE_DOCUMENT"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:SAVE"));
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
					return resources.getString("FILE_FILTER_PDF");
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
						String.format(resources.getString("OVERWRITE_FILE_TEXT"), file.getAbsolutePath()),
						resources.getString("OVERWRITE_FILE_TITLE"),
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
			super(resources.getString("ZOOM_OUT"), Icons.ZOOM_OUT);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("ZOOM_OUT"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:ZOOM_OUT"));
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
			super(resources.getString("ZOOM_IN"), Icons.ZOOM_IN);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("ZOOM_IN"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:ZOOM_IN"));
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
			super(resources.getString("CLEAR_LIST"), Icons.EDIT_CLEAR);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("CLEAR_LIST"));
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			StandardListModel<Page> model = parent.mainList.getModel();
			if (
				(model.getSize() > 0) &&
				(JOptionPane.showConfirmDialog(
					parent,
					resources.getString("CONFIRM_CLEAR_LIST_TEXT"),
					resources.getString("CONFIRM_CLEAR_LIST_TITLE"),
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
			super(resources.getString("DELETE"), Icons.TRASH);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("DELETE"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:DELETE"));
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
			List<Page> model = list.getModel();
			int[] selected = list.getSelectedIndices();
			Arrays.sort(selected);
			for (int i = selected.length-1; i >= 0; i--) {
				model.remove(selected[i]);
			}
		}
	}

	public static class AboutAction extends AbstractAction {
		private static final long serialVersionUID = -6505580153294146608L;
		private final PdfJumbler parent;
		
		public AboutAction(PdfJumbler parent) {
			super(resources.getString("ABOUT"));
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("ABOUT"));
		}
			
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(
				parent,
				String.format(
					resources.getString("ABOUT_TEXT"),
					PdfJumbler.VERSION_STRING,
					PdfProcessingFactory.getEditor().getClass().getCanonicalName(),
					PdfProcessingFactory.getRenderer().getClass().getCanonicalName()
				),
				resources.getString("ABOUT_TITLE"),
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
				ex.printStackTrace();
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
				ex.printStackTrace();
				JOptionPane.showMessageDialog(parent, ex.getLocalizedMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static class WrenchAction extends AbstractAction {
		private static final long serialVersionUID = -3880011502734901446L;
		private final PdfJumbler parent;
		public JPopupMenu menu = new JPopupMenu();
		private Component component = null;

		public WrenchAction(PdfJumbler parent) {
			super(resources.getString("WRENCH"), Icons.WRENCH);
			this.parent = parent;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("WRENCH"));
			populate();
		}

		private void populate() {
			menu.add(parent.undoAction);
			menu.add(parent.redoAction);
			
			menu.addSeparator();
			
			ButtonGroup editorGroup = new ButtonGroup();
			JMenu editorMenu = new JMenu(resources.getString("WRENCH_EDITOR"));
			for (Class<? extends PdfEditor> cls : PdfProcessingFactory.getAvailableEditors()) {
				JRadioButtonMenuItem item = new JRadioButtonMenuItem(new Actions.ChangeEditorAction(parent, cls));
				editorGroup.add(item);
				editorMenu.add(item);
				if (PdfProcessingFactory.getEditorClass().equals(cls)) {
					item.setSelected(true);
				}
			}
			menu.add(editorMenu);

			ButtonGroup rendererGroup = new ButtonGroup();
			JMenu rendererMenu = new JMenu(resources.getString("WRENCH_RENDERER"));
			for (Class<? extends PdfRenderer> cls : PdfProcessingFactory.getAvailableRenderers()) {
				JRadioButtonMenuItem item = new JRadioButtonMenuItem(new Actions.ChangeRendererAction(parent, cls));
				rendererGroup.add(item);
				rendererMenu.add(item);
				if (PdfProcessingFactory.getRendererClass().equals(cls)) {
					item.setSelected(true);
				}
			}
			menu.add(rendererMenu);

			menu.addSeparator();
			menu.add(new Actions.AboutAction(parent));
		}

		public void setComponent(Component c) {
			this.component = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			menu.show(component, 0, component.getHeight());
		}		
	};

	public static class MoveUpAction extends AbstractAction {
		private static final long serialVersionUID = 4204383549863556707L;
		
		public MoveUpAction() {
			super(resources.getString("MOVE_UP"));
			putValue(Action.SHORT_DESCRIPTION, resources.getString("MOVE_UP"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:MOVE_UP"));
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!(e.getSource() instanceof JDragDropList<?>)) {
				return;
			}

			@SuppressWarnings("unchecked")
			JDragDropList<Object> list = (JDragDropList<Object>)e.getSource();
			int index = list.getSelectedIndex();
			if (index <= 0) {
				return;
			}

			List<Object> model = list.getModel();
			Object obj = model.get(index);
			model.remove(index);
			model.add(index-1, obj);

			list.setSelectedIndex(index-1);
		}
	}
	
	public static class MoveDownAction extends AbstractAction {
		private static final long serialVersionUID = -4468213711264218766L;

		public MoveDownAction() {
			super(resources.getString("MOVE_DOWN"));
			putValue(Action.SHORT_DESCRIPTION, resources.getString("MOVE_DOWN"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:MOVE_DOWN"));
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!(e.getSource() instanceof JDragDropList<?>)) {
				return;
			}

			@SuppressWarnings("unchecked")
			JDragDropList<Object> list = (JDragDropList<Object>)e.getSource();
			List<Object> model = list.getModel();

			int index = list.getSelectedIndex();
			if ((index < 0) || (index >= model.size()-1)) {
				return;
			}

			Object obj = model.get(index);
			model.remove(index);
			model.add(index+1, obj);

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

	public static class OpenDocumentWorker extends SwingWorker<Void,List<Page>> {
		private final UndoableList<Page> model;
		private volatile int insertPos;
		private final Collection<File> files;

		public OpenDocumentWorker(UndoableList<Page> model, int insertPos, Collection<File> files) {
			this.model = model;
			this.insertPos = (insertPos < 0) ? model.size() : insertPos;
			this.files = files;
			
			// execute is final :/
			model.beginCompoundEdit("Open");
		}

		@Override
		protected Void doInBackground() throws Exception {
			setProgress(0);

			int nFiles = 0;
			for (File file : files) {
				firePropertyChange("note", "", file.getName());

				publish(DocumentManager.getPages(file));

				nFiles++;
				setProgress((int)(100 * (nFiles / (double)files.size())));
			}
			

			return null;
		}

		@Override
		protected void process(List<List<Page>> pages) {
			for (List<Page> pageList : pages) {
				for (Page page : pageList) {
					model.add(insertPos++, page);
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

		public SaveDocumentWorker(List<Page> pages, File file) {
			this.pages = pages;
			this.file = file;
		}

		@Override
		protected Void doInBackground() throws Exception {
			firePropertyChange("note", "", file.getName());
			
			// TODO: Detect links!
			// There seems to be no really good way to do this with
			// Java 6, but Java 7's java.nio.file could help...
			
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
			super(resources.getString("UNDO"));
			this.undoManager = undoManager;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("UNDO"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:UNDO"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.err.println(undoManager.canUndo());
			if (undoManager.canUndo()) {
				//System.err.println("Undoing: " + undoManager.getPresentationName());
				undoManager.undo();
				//System.err.println("Next: " + undoManager.getPresentationName());
			}
		}
	}
	
	public static class RedoAction extends AbstractAction {
		private static final long serialVersionUID = -4360624396939210557L;
		private final UndoManager undoManager;
		
		public RedoAction(UndoManager undoManager) {
			super(resources.getString("REDO"));
			this.undoManager = undoManager;
			putValue(Action.SHORT_DESCRIPTION, resources.getString("REDO"));
			putValue(Action.ACCELERATOR_KEY, resources.getObject("ACCELERATOR:REDO"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}
	}
}

/**
 * A special UndoManager that adds edits only if the
 * last edit is not the same as the new one.
 * This is needed for move operations between lists
 * (implemented as remove + add compound edits) and
 * is simpler than using insignificant edits. 
 *  
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
class UniqueUndoManager extends UndoManager {
	private static final long serialVersionUID = -2740016241678747836L;

	@Override
	public boolean addEdit(UndoableEdit edit) {
		if ((lastEdit() != null) && lastEdit().equals(edit)) {
			return false;
		} else {
			return super.addEdit(edit);
		}
	}
}
