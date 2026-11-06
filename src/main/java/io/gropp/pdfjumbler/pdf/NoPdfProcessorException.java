package io.gropp.pdfjumbler.pdf;

public class NoPdfProcessorException extends Exception {
    private static final long serialVersionUID = 1L;

    public enum Kind {
        EDITOR,
        RENDERER
    }

    private final Kind kind;

    public NoPdfProcessorException(Kind kind) {
        super("No usable PDF " + kind.name().toLowerCase() + " found.");
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }
}
