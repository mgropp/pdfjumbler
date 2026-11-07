package io.gropp.pdfjumbler.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;

public final class DocumentSaver {
    @FunctionalInterface
    public interface PageWriter {
        void write(List<Page> pages, File file) throws IOException;
    }

    private final DocumentManager documentManager;
    private final PageWriter writer;

    public DocumentSaver(DocumentManager documentManager, PageWriter writer) {
        this.documentManager = documentManager;
        this.writer = writer;
    }

    public void save(List<Page> pages, File target, BooleanSupplier cancelled) throws IOException {
        if (pages.isEmpty()) {
            throw new IOException("Empty document.");
        }

        Path targetPath = target.getAbsoluteFile().toPath();
        Path temporary = Files.createTempFile(targetPath.getParent(), ".pdfjumbler-", ".tmp");
        try {
            writer.write(pages, temporary.toFile());
            if (cancelled.getAsBoolean()) {
                throw new CancellationException();
            }
            documentManager.moveAsideIfOpen(target);
            replace(temporary, targetPath);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void replace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
