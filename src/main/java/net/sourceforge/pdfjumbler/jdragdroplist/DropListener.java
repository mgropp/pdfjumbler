package net.sourceforge.pdfjumbler.jdragdroplist;

import javax.swing.TransferHandler;

/**
 * @author Martin Gropp
 */
public interface DropListener {
	/**
	 * Called for all possible drops, including list items.
	 * Return false to reject a drop.
	 * 
	 * @param sender
	 *   The JDragDropList.
	 * @param info
	 * @return
	 */
	boolean acceptDrop(Object sender, TransferHandler.TransferSupport info);
	
	/**
	 * Called for drops the list can't handle itself (i.e. for everything
	 * except other JDDL list items).
	 * Return true iff the drop succeeded.
	 * 
	 * @param sender
	 *   The JDragDropList.
	 * @param info
	 * @return
	 */
	boolean handleDrop(Object sender, TransferHandler.TransferSupport info);
}
