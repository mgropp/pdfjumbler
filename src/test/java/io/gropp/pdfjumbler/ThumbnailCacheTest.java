package io.gropp.pdfjumbler;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.pdf.Page;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ThumbnailCacheTest {
    private final File file = new File("doc.pdf");
    private final Queue<Runnable> backgroundTasks = new ArrayDeque<>();
    private final List<Page> loadedPages = new ArrayList<>();
    private final AtomicInteger loadedNotifications = new AtomicInteger();
    private boolean failLoading;

    private ThumbnailCache cache(int capacity) {
        return new ThumbnailCache(
                capacity,
                (page, width, height) -> {
                    loadedPages.add(page);
                    if (failLoading) {
                        throw new IOException("broken");
                    }
                    return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                },
                backgroundTasks::add,
                Runnable::run,
                loadedNotifications::incrementAndGet);
    }

    private void runBackgroundTasks() {
        while (!backgroundTasks.isEmpty()) {
            backgroundTasks.remove().run();
        }
    }

    @Test
    void returnsNothingUntilTheImageIsLoaded() {
        ThumbnailCache cache = cache(10);

        assertThat(cache.get(new Page(file, 0), 10, 20)).isEmpty();
        assertThat(loadedPages).isEmpty();

        runBackgroundTasks();

        assertThat(cache.get(new Page(file, 0), 10, 20)).isPresent();
        assertThat(loadedNotifications).hasValue(1);
    }

    @Test
    void requestsEachImageOnlyOnceWhileLoading() {
        ThumbnailCache cache = cache(10);

        cache.get(new Page(file, 0), 10, 20);
        cache.get(new Page(file, 0), 10, 20);
        runBackgroundTasks();

        assertThat(loadedPages).hasSize(1);
    }

    @Test
    void distinguishesRotationAndSize() {
        ThumbnailCache cache = cache(10);
        Page page = new Page(file, 0);
        cache.get(page, 10, 20);
        runBackgroundTasks();
        page.setRotation(90);

        assertThat(cache.get(page, 10, 20)).isEmpty();
        assertThat(cache.get(new Page(file, 0), 20, 40)).isEmpty();
        assertThat(cache.get(new Page(file, 0), 10, 20)).isPresent();
    }

    @Test
    void rendersTheRotationFromRequestTime() {
        ThumbnailCache cache = cache(10);
        Page page = new Page(file, 0);
        page.setRotation(90);
        cache.get(page, 10, 20);
        page.setRotation(180);

        runBackgroundTasks();

        assertThat(loadedPages.get(0).getRotation()).isEqualTo(90);
    }

    @Test
    void evictsLeastRecentlyUsedImages() {
        ThumbnailCache cache = cache(2);
        for (int i = 0; i < 3; i++) {
            cache.get(new Page(file, i), 10, 20);
            runBackgroundTasks();
        }

        assertThat(cache.get(new Page(file, 0), 10, 20)).isEmpty();
        assertThat(cache.get(new Page(file, 2), 10, 20)).isPresent();
    }

    @Test
    void doesNotRetryFailedPages() {
        failLoading = true;
        ThumbnailCache cache = cache(10);

        cache.get(new Page(file, 0), 10, 20);
        runBackgroundTasks();
        cache.get(new Page(file, 0), 10, 20);
        runBackgroundTasks();

        assertThat(loadedPages).hasSize(1);
        assertThat(loadedNotifications).hasValue(0);
    }

    @Test
    void clearDiscardsImagesAndResultsStillLoading() {
        ThumbnailCache cache = cache(10);
        cache.get(new Page(file, 0), 10, 20);
        cache.clear();
        runBackgroundTasks();

        assertThat(loadedNotifications).hasValue(0);
        assertThat(cache.get(new Page(file, 0), 10, 20)).isEmpty();
    }

    @Test
    void failedPagesAreRetriedAfterClear() {
        failLoading = true;
        ThumbnailCache cache = cache(10);
        cache.get(new Page(file, 0), 10, 20);
        runBackgroundTasks();
        failLoading = false;
        cache.clear();

        cache.get(new Page(file, 0), 10, 20);
        runBackgroundTasks();

        assertThat(cache.get(new Page(file, 0), 10, 20)).isPresent();
    }
}
