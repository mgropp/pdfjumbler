package io.gropp.pdfjumbler.pdf.plugin;

import java.io.Serial;

public class PluginException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public PluginException(String msg) {
        super(msg);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }

    public PluginException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
