package net.sourceforge.pdfjumbler.jdragdroplist;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Gropp
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
	private final List<T> values;

	public JDDLTransferData(JDragDropList<T> list) {
		this.sourceList = list;
		this.indices = list.getSelectedIndices().clone();
		this.values = new ArrayList<>(list.getSelectedValuesList());
	}

	public JDragDropList<T> getSourceList() {
		return sourceList;
	}

	public int[] getIndices() {
		return indices;
	}

	public List<T> getValuesList() {
		return values;
	}
}
