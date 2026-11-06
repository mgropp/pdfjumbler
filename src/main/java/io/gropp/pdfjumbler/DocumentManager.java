package io.gropp.pdfjumbler;

import io.gropp.pdfjumbler.pdf.Page;
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

    private final PageCounter pageCounter;
    private final OpenFileCloser openFileCloser;
    private final Map<File, List<Page>> pageMap = new HashMap<>();

    public DocumentManager(PageCounter pageCounter, OpenFileCloser openFileCloser) {
        this.pageCounter = pageCounter;
        this.openFileCloser = openFileCloser;
    }

    public synchronized List<Page> getPages(File file) throws IOException {
        List<Page> pages = pageMap.get(file);
        if (pages != null) {
            return pages;
        }

        int pageCount = pageCounter.getNumberOfPages(file);
        List<Page> newPages = new ArrayList<>(pageCount);
        for (int i = 0; i < pageCount; i++) {
            newPages.add(new Page(file, i));
        }

        pages = Collections.unmodifiableList(newPages);
        pageMap.put(file, pages);
        return pages;
    }

    public synchronized Collection<File> getAllFiles() {
        return List.copyOf(pageMap.keySet());
    }

    public synchronized void moveAsideIfOpen(File file) throws IOException {
        if (!file.exists() || !pageMap.containsKey(file)) {
            return;
        }

        File tempFile = File.createTempFile("pdfjumbler", ".pdf");
        tempFile.deleteOnExit();
        openFileCloser.closeOpenFiles();
        Files.move(file.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        List<Page> pages = pageMap.remove(file);
        for (Page page : pages) {
            page.setFile(tempFile);
        }
        pageMap.put(tempFile, pages);
    }
}
