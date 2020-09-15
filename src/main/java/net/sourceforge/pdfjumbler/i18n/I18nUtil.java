package net.sourceforge.pdfjumbler.i18n;

import java.awt.Toolkit;
import java.util.ResourceBundle;

public class I18nUtil {
	static final int MENU_SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

	public static String getString(ResourceBundle bundle, String key, String fallback) {
		return bundle.containsKey(key) ? bundle.getString(key) : fallback;
	}
}
