package net.sourceforge.pdfjumbler.jdragdroplist;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.undo.CompoundEdit;

/**
 * A JList that supports drag & drop.
 * 
 * @author Martin Gropp
 */
public class JDragDropList<T> extends JList<T> {
	private static final long serialVersionUID = -6709912327369063711L;

	private DropListener dropListener = null;
	
	public JDragDropList(UndoableListModel<T> model) {
		super(model);
		
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setDragEnabled(true);
		setDropMode(DropMode.INSERT);
		setTransferHandler(new JDDLTransferHandler());
	}

	public DropListener getDropListener() {
		return dropListener;
	}

	public void setDropListener(DropListener dropListener) {
		this.dropListener = dropListener;
	}
	
	private final class JDDLTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 8850624713729645277L;
		
		private void copyItems(JDragDropList<? extends T> sourceList, JDragDropList<? super T> targetList, List<T> sourceObjects, int destIndex) {
			UndoableListModel<? super T> model = targetList.getModel();
		
			model.beginCompoundEdit("Copy");
			try {
				for (T obj : sourceObjects) {
					model.add(destIndex++, obj);
				}
			}
			finally {
				model.endCompoundEdit();
			}
				
			targetList.setSelectionInterval(destIndex - sourceObjects.size(), destIndex - 1);
		}
		
		private void moveItems(JDragDropList<? extends T> sourceList, JDragDropList<? super T> targetList, int[] sourceIndices, int destIndex) {
			UndoableListModel<? extends T> sourceModel = sourceList.getModel();
			UndoableListModel<? super T> targetModel = targetList.getModel();
			
			CompoundEdit edit = new CompoundEdit() {
				private static final long serialVersionUID = -8658162613932247385L;

				@Override
				public String getPresentationName() {
					return "Move";
				}
			};

			sourceModel.beginCompoundEdit(edit);
			try {
				targetModel.beginCompoundEdit(edit);
				try {
					for (int i = 0; i < sourceIndices.length; i++) {
						int index = sourceIndices[i];
						T item = sourceModel.get(index);
						sourceModel.remove(index);
						
						for (int j = i + 1; j < sourceIndices.length; j++) {
							int value = sourceIndices[j];
							if (value > index) {
								sourceIndices[j]--;
							}
							if (sourceModel == targetModel) {
								if (value >= destIndex) {
									sourceIndices[j]++;
								}
							}
						}
						if ((sourceModel == targetModel) && (index < destIndex)) {
							destIndex--;
						}
						
						targetModel.add(destIndex, item);
						destIndex++;
					}
				}
				finally {
					targetModel.endCompoundEdit();
				}
			}
			finally {
				sourceModel.endCompoundEdit();
			}

			targetList.setSelectionInterval(destIndex - sourceIndices.length, destIndex - 1);

			sourceList.updateUI();
			targetList.updateUI();
		}

		@SuppressWarnings("unchecked")
		private JDDLTransferData<T> getData(TransferHandler.TransferSupport info) {
			JDDLTransferData<T> data;
			try {
				data = (JDDLTransferData<T>)info.getTransferable().getTransferData(JDDLTransferData.DATA_FLAVOR);
			}
			catch (UnsupportedFlavorException e) {
				throw new AssertionError(e);
			}
			catch (IOException e) {
				throw new AssertionError(e);
			}

			return data;
		}

		private boolean canImportHere(TransferHandler.TransferSupport info) {
			return
				((info.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) &&
				info.isDataFlavorSupported(JDDLTransferData.DATA_FLAVOR);	
		}
		
		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {
			if (JDragDropList.this.dropListener != null) {
				return JDragDropList.this.dropListener.acceptDrop(JDragDropList.this, info);
			} else {
				return canImportHere(info);
			}
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}
			if (!canImportHere(info)) {
				if (
					(JDragDropList.this.dropListener != null) &&
					JDragDropList.this.dropListener.acceptDrop(JDragDropList.this, info)
				) {
					return JDragDropList.this.dropListener.handleDrop(JDragDropList.this, info);
				} else {
					return false;
				}
			}

			JDDLTransferData<T> data = getData(info);
			int destIndex = JDragDropList.this.getDropLocation().getIndex();

			if ((info.getDropAction() & DnDConstants.ACTION_COPY) != 0) {
				copyItems(data.getSourceList(), JDragDropList.this, data.getValuesList(), destIndex);
			} else if ((info.getDropAction() & DnDConstants.ACTION_MOVE) != 0) {
				moveItems(data.getSourceList(), JDragDropList.this, data.getIndices(), destIndex);	
			} else {
				return false;
			}
			
			return true;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return DnDConstants.ACTION_COPY_OR_MOVE;
			//return DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK | DnDConstants.ACTION_MOVE | DnDConstants.ACTION_REFERENCE;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			@SuppressWarnings("unchecked")
			final JDDLTransferData<T> data = new JDDLTransferData<T>((JDragDropList<T>)c);
			return new Transferable() {
				@Override
				public JDDLTransferData<T> getTransferData(DataFlavor flavor)
						throws UnsupportedFlavorException {
					if (!flavor.equals(JDDLTransferData.DATA_FLAVOR)) {
						throw new UnsupportedFlavorException(flavor);
					}

					return data;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { JDDLTransferData.DATA_FLAVOR };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return flavor.equals(JDDLTransferData.DATA_FLAVOR);
				}
			};
		}
	}
	
	@Override
	public UndoableListModel<T> getModel() {
		return (UndoableListModel<T>)super.getModel();
	}

	@Override
	public void setModel(ListModel<T> model) {
		if (model instanceof UndoableListModel) {
			super.setModel(model);
		} else {
			throw new IllegalArgumentException("Unsupported model type.");
		}
	}
	
	@Override
	@Deprecated
	@SuppressWarnings("unchecked")
	public T[] getSelectedValues() {
		return (T[])super.getSelectedValues();
	}
}
