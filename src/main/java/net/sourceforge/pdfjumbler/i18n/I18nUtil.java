package net.sourceforge.pdfjumbler.i18n;

import java.util.ResourceBundle;

public class I18nUtil {
	public static String getString(ResourceBundle bundle, String key, String fallback) {
		return bundle.containsKey(key) ? bundle.getString(key) : fallback;
	}
}
