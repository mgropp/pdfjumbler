package io.gropp.pdfjumbler.ui;

import java.awt.Component;
import javax.swing.JOptionPane;
import org.jspecify.annotations.Nullable;
import org.tinylog.Logger;

public final class ErrorDialog {
    private ErrorDialog() {}

    public static void show(@Nullable Component parent, Throwable throwable) {
        Logger.error(throwable);
        JOptionPane.showMessageDialog(
                parent,
                throwable.getLocalizedMessage(),
                throwable.getClass().getSimpleName(),
                JOptionPane.ERROR_MESSAGE);
    }
}
