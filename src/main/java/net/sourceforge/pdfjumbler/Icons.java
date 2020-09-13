package net.sourceforge.pdfjumbler;

import javax.swing.ImageIcon;
import java.util.Objects;

public class Icons {
	public static final ImageIcon PDFJUMBLER = getIcon("pdfjumbler.png");
	public static final ImageIcon ZOOM_IN = getIcon("zoom-in.png");
	public static final ImageIcon ZOOM_OUT = getIcon("zoom-out.png");
	public static final ImageIcon DOCUMENT_OPEN = getIcon("document-open.png");
	public static final ImageIcon DOCUMENT_SAVE = getIcon("document-save.png");
	public static final ImageIcon EDIT_DELETE = getIcon("edit-delete.png");
	public static final ImageIcon HELP_ABOUT = getIcon("help-about.png");
	public static final ImageIcon MENU = getIcon("menu.png");
	public static final ImageIcon ROTATE_CW = getIcon("rotate-cw.png");
	public static final ImageIcon ROTATE_CCW = getIcon("rotate-ccw.png");

	public interface Size16 {
		ImageIcon DOCUMENT_OPEN = getIcon("document-open-16.png");
		ImageIcon DOCUMENT_SAVE = getIcon("document-save-16.png");
	}
	
	private static ImageIcon getIcon(String name) {
		return new ImageIcon(Objects.requireNonNull(
			Icons.class.getClassLoader().getResource(name)
		));
	}
}
