package io.gropp.pdfjumbler.pdf.plugin;

import javax.swing.JOptionPane;

final class SwingStartupErrors implements StartupErrors {
    @Override
    public void fatal(String message, String title, int exitStatus) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        System.exit(exitStatus);
    }
}
