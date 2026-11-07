package io.gropp.pdfjumbler.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentManager {
    @FunctionalInterface
    public interface PageCounter {
        int getNumberOfPages(File file) throws IOException;
    }

    @FunctionalInterface
    public interface OpenFileCloser {
        void closeOpenFiles() throws IOException;
    }

    private record OpenDocument(int pageCount, List<Page> issuedPages) {}

    private final PageCounter pageCounter;
    private final OpenFileCloser openFileCloser;
    private final Map<File, OpenDocument> documents = new HashMap<>();

    public DocumentManager(PageCounter pageCounter, OpenFileCloser openFileCloser) {
        this.pageCounter = pageCounter;
        this.openFileCloser = openFileCloser;
    }

    public synchronized List<Page> getPages(File file) throws IOException {
        OpenDocument document = documents.get(file);
        if (document == null) {
            document = new OpenDocument(pageCounter.getNumberOfPages(file), new ArrayList<>());
            documents.put(file, document);
        }

        List<Page> pages = new ArrayList<>(document.pageCount());
        for (int i = 0; i < document.pageCount(); i++) {
            pages.add(new Page(file, i));
        }
        document.issuedPages().addAll(pages);
        return Collections.unmodifiableList(pages);
    }

    public synchronized Collection<File> getAllFiles() {
        return List.copyOf(documents.keySet());
    }

    public synchronized void moveAsideIfOpen(File file) throws IOException {
        if (!file.exists() || !documents.containsKey(file)) {
            return;
        }

        File tempFile = File.createTempFile("pdfjumbler", ".pdf");
        tempFile.deleteOnExit();
        openFileCloser.closeOpenFiles();
        Files.move(file.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        OpenDocument document = documents.remove(file);
        for (Page page : document.issuedPages()) {
            page.setFile(tempFile);
        }
        documents.put(tempFile, document);
    }
}
