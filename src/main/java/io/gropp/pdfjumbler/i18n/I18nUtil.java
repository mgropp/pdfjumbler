package io.gropp.pdfjumbler.i18n;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.util.ResourceBundle;

public class I18nUtil {
    static int menuShortcutKey() {
        if (GraphicsEnvironment.isHeadless()) {
            return InputEvent.CTRL_DOWN_MASK;
        }
        return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    }

    public static String getString(ResourceBundle bundle, String key, String fallback) {
        return bundle.containsKey(key) ? bundle.getString(key) : fallback;
    }
}
