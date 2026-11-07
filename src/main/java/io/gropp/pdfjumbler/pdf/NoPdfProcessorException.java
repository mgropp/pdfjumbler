package io.gropp.pdfjumbler.pdf;

import java.io.Serial;
import java.util.Locale;

public class NoPdfProcessorException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public enum Kind {
        EDITOR,
        RENDERER
    }

    private final Kind kind;

    public NoPdfProcessorException(Kind kind) {
        super("No usable PDF " + kind.name().toLowerCase(Locale.ROOT) + " found.");
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }
}
