package net.sourceforge.pdfjumbler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.undo.UndoManager;

import net.sourceforge.pdfjumbler.actions.*;
import net.sourceforge.pdfjumbler.i18n.I18nKeys;
import net.sourceforge.pdfjumbler.i18n.PdfJumblerResources;
import net.sourceforge.pdfjumbler.pdf.PdfProcessingFactory;

import org.tinylog.Logger;

/**
 * PdfJumbler main class.
 * 
 * @author Martin Gropp
 */
public class PdfJumbler extends JFrame {
	private static final long serialVersionUID = 4382647271800905977L;
	public static final int VERSION = 20200912;
	public static final String VERSION_STRING = "2020-09-12";
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());

	private static PdfJumbler instance = null; 

	private final PdfList mainList;
	private final PdfList secondaryList;
	private final UndoManager undoManager = new UniqueUndoManager();
	private final Actions actions;

	public PdfList getMainPdfList() {
		return mainList;
	}

	public PdfList getSecondaryPdfList() {
		return secondaryList;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public Actions getActions() {
		return actions;
	}

	public static void openFiles(PdfList list, int insertPos, Collection<File> files) {
		if (files.size() > 0) {
			try {
				ProgressDialog.run(
					new OpenDocumentWorker(
						list.getModel(),
						insertPos,
						files
					),
					PdfJumbler.instance
				);
			}
			catch (Exception e) {
				Logger.error(e);
				JOptionPane.showMessageDialog(instance, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	static void openFiles(PdfList list, int insertPos, String... fileNames) {
		ArrayList<File> files = new ArrayList<>(fileNames.length);
		for (String fileName : fileNames) {
			files.add(new File(fileName));
		}
		openFiles(list, insertPos, files);
	}

	public static void saveFile(PdfList list, File file) {
		try {
			ProgressDialog.run(
				new SaveDocumentWorker(
					list.getModel(),
					file
				),
				instance
			);
		}
		catch (Exception e) {
			Logger.error(e);
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
				Logger.error(e);
			}
		} else {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception e) {
				// ignore
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
										Logger.error(e);
										JOptionPane.showMessageDialog(
											PdfJumbler.this,
											e.getLocalizedMessage(),
											e.getClass().getSimpleName(),
											JOptionPane.ERROR_MESSAGE
										);
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
				Logger.error(e);
				JOptionPane.showMessageDialog(
					this,
					e.getLocalizedMessage(),
					e.getClass().getSimpleName(),
					JOptionPane.ERROR_MESSAGE
				);
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

	private PdfJumbler(String[] files) {
		Logger.info("PdfJumbler is starting");

		PdfJumbler.instance = this;

		installExceptionHandler();
		setTitle(getClass().getSimpleName());
		setIconImage(Icons.PDF_JUMBLER.getImage());
		setLookAndFeel();

		// Prepare toolbars
		JToolBar toolBar = new JToolBar();
		JToolBar toolBar2 = new JToolBar();
		toolBar.setFloatable(false);
		toolBar2.setFloatable(false);
		
		// Lists
		mainList = new PdfList();
		mainList.setDisplayMessage(resources.getString(I18nKeys.LIST_DROP_PDFS_TO_EDIT));
		JScrollPane mainPane = new JScrollPane(mainList);
		
		secondaryList = new PdfList();
		secondaryList.setDisplayMessage(resources.getString(I18nKeys.LIST_CLIPBOARD_EMPTY));
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
		actions = new Actions(this);

		// Toolbar
		toolBar.add(actions.getDocOpenAction());
		toolBar.add(actions.getDocSaveAction());
		toolBar.addSeparator();
		toolBar.add(actions.getZoomOutAction());
		toolBar.add(actions.getZoomInAction());
		toolBar.addSeparator();
		JButton delButton = toolBar.add(actions.getDeleteAction());
		delButton.setDropTarget(new DropTarget(delButton, new TrashDropTargetListener()));
		toolBar.addSeparator();
		toolBar.add(new RotateClockwiseAction(this));

		toolBar.add(Box.createHorizontalGlue());
		MenuAction menuAction = new MenuAction(this);
		menuAction.setComponent(toolBar.add(menuAction));

		add(toolBar, BorderLayout.NORTH);

		// Toolbar 2
		toolBar2.add(Box.createHorizontalGlue());
		toolBar2.add(actions.getDocOpenAction2());
		toolBar2.add(actions.getDocSaveAction2());

		// Open files
		openFiles(mainList, -1, files);

		// Listen for editor/renderer changes
		PdfProcessingFactory.addProcessorListener(new ReloadingProcessorListener(this));

		// Register accelerators
		registerAccelerators(
			getRootPane(),
			actions.getDocOpenAction(), actions.getDocSaveAction(),
			actions.getZoomInAction(), actions.getZoomOutAction(),
			actions.getUndoAction(), actions.getRedoAction(),
			actions.getRotateClockwiseAction(),
			actions.getRotateCounterClockwiseAction()
		);
		registerAccelerators(
			mainList,
			actions.getMoveUpAction(), actions.getMoveDownAction(), actions.getDeleteAction()
		);
		registerAccelerators(
			secondaryList,
			actions.getMoveUpAction(),
			actions.getMoveDownAction(),
			actions.getDeleteAction()
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

