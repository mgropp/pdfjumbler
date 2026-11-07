package io.gropp.pdfjumbler.ui;

import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.jspecify.annotations.Nullable;

public final class SwingDialogs implements Dialogs {
    private final @Nullable Component parent;
    private @Nullable String lastOpenedFileLocation;
    private @Nullable String lastSavedLocation;

    public SwingDialogs(@Nullable Component parent) {
        this.parent = parent;
    }

    @Override
    public boolean confirmWarning(String text, String title) {
        return JOptionPane.showConfirmDialog(
                        parent, text, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)
                == JOptionPane.OK_OPTION;
    }

    @Override
    public void showError(@Nullable String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public Optional<List<File>> chooseFilesToOpen() {
        JFileChooser chooser = new JFileChooser(lastOpenedFileLocation);
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new PdfFileFilter() {
            @Override
            public boolean accept(File file) {
                lastOpenedFileLocation = file.getParent();
                return file.isDirectory() || (file.exists() && file.isFile() && file.canRead() && isPdf(file));
            }
        });

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return Optional.of(Arrays.asList(chooser.getSelectedFiles()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> chooseFileToSave() {
        JFileChooser chooser = new JFileChooser(lastSavedLocation);
        chooser.setFileFilter(new PdfFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || (hasExistingParent(file) && isWritablePdf(file));
            }
        });

        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            lastSavedLocation = file.getParent();
            return Optional.of(file);
        }
        return Optional.empty();
    }

    private static boolean hasExistingParent(File file) {
        File parent = file.getParentFile();
        return parent != null && parent.exists();
    }

    private static boolean isWritablePdf(File file) {
        return (!file.exists() || file.isFile()) && file.canWrite() && isPdf(file);
    }

    private static boolean isPdf(File file) {
        return file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf");
    }

    private abstract static class PdfFileFilter extends FileFilter {
        @Override
        public String getDescription() {
            return PdfJumblerResources.getResources().getString(I18nKeys.FILE_FILTER_PDF);
        }
    }
}
