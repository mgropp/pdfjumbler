package net.sourceforge.pdfjumbler;

import javax.swing.ImageIcon;

public class Icons {
	public static final ImageIcon PDFJUMBLER = getIcon("pdfjumbler.png");
	public static final ImageIcon TRASH = getIcon("trash.png");
	public static final ImageIcon ZOOM_IN = getIcon("zoom-in.png");
	public static final ImageIcon ZOOM_OUT = getIcon("zoom-out.png");
	public static final ImageIcon DOCUMENT_OPEN = getIcon("document-open.png");
	public static final ImageIcon DOCUMENT_SAVE = getIcon("document-save.png");
	public static final ImageIcon EDIT_DELETE = getIcon("edit-delete.png");
	public static final ImageIcon EDIT_CLEAR = getIcon("edit-clear.png");
	public static final ImageIcon HELP_ABOUT = getIcon("help-about.png");
	public static final ImageIcon WRENCH = getIcon("wrench.png");
	
	public interface Size16 {
		ImageIcon DOCUMENT_OPEN = getIcon("document-open-16.png");
		ImageIcon DOCUMENT_SAVE = getIcon("document-save-16.png");
	}
	
	private static ImageIcon getIcon(String name) {
		return new ImageIcon(Icons.class.getClassLoader().getResource(name));
	}
}
