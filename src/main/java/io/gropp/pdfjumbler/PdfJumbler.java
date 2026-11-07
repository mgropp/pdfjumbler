package io.gropp.pdfjumbler;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.UIScale;
import io.gropp.pdfjumbler.config.UserPreferences;
import io.gropp.pdfjumbler.pdf.plugin.PdfProcessingSetup;
import io.gropp.pdfjumbler.ui.ErrorDialog;
import io.gropp.pdfjumbler.ui.MainFrame;
import java.io.File;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class PdfJumbler {
    private static final int INITIAL_WIDTH = 340;
    private static final int INITIAL_HEIGHT = 600;

    private PdfJumbler() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> start(args));
    }

    private static void start(String[] files) {
        try {
            FlatLightLaf.setup();
            MainFrame frame = new MainFrame(UserPreferences.forCurrentUser(), PdfProcessingSetup.createFactory());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(UIScale.scale(INITIAL_WIDTH), UIScale.scale(INITIAL_HEIGHT));
            frame.setVisible(true);
            frame.openFiles(Arrays.stream(files).map(File::new).toList());
        } catch (Exception e) {
            ErrorDialog.show(null, e);
        }
    }
}
