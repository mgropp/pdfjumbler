package io.gropp.pdfjumbler.pdf.plugin;

@FunctionalInterface
interface StartupErrors {
    void fatal(String message, String title, int exitStatus);
}
