package net.sourceforge.pdfjumbler.jdragdroplist;

import java.awt.datatransfer.DataFlavor;

/**
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public final class JDDLTransferData<T> {
	public static final DataFlavor DATA_FLAVOR;

	static {
		try {
			DATA_FLAVOR = new DataFlavor(
				DataFlavor.javaJVMLocalObjectMimeType +
				";class=" + JDDLTransferData.class.getCanonicalName()
			);
		}
		catch (ClassNotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	private final JDragDropList<T> sourceList;
	private final int[] indices;
	private final T[] values;

	public JDDLTransferData(JDragDropList<T> list) {
		this.sourceList = list;
		this.indices = (int[])list.getSelectedIndices().clone();
		this.values = list.getSelectedValues().clone();
	}

	public JDragDropList<T> getSourceList() {
		return sourceList;
	}

	public int[] getIndices() {
		return indices;
	}

	public T[] getValues() {
		return values;
	}
}
