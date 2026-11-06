package io.gropp.pdfjumbler;

import io.gropp.pdfjumbler.pdf.Page;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import org.jspecify.annotations.Nullable;
import org.tinylog.Logger;

final class ThumbnailCache {
    @FunctionalInterface
    interface Loader {
        Image load(Page page, int maxWidth, int maxHeight) throws IOException;
    }

    private record Key(File file, int pageIndex, int rotation, int width, int height) {}

    private final Loader loader;
    private final Executor loadExecutor;
    private final Executor resultExecutor;
    private final Runnable onLoaded;
    private final Map<Key, Image> images;
    private final Set<Key> pending = new HashSet<>();
    private final Set<Key> failed = new HashSet<>();
    private int generation;

    ThumbnailCache(int capacity, Loader loader, Executor loadExecutor, Executor resultExecutor, Runnable onLoaded) {
        this.loader = loader;
        this.loadExecutor = loadExecutor;
        this.resultExecutor = resultExecutor;
        this.onLoaded = onLoaded;
        this.images = new LinkedHashMap<>(16, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Key, Image> eldest) {
                return size() > capacity;
            }
        };
    }

    synchronized Optional<Image> get(Page page, int maxWidth, int maxHeight) {
        Key key = new Key(page.getFile(), page.getIndex(), page.getRotation(), maxWidth, maxHeight);
        Image image = images.get(key);
        if (image != null) {
            return Optional.of(image);
        }
        if (!failed.contains(key) && pending.add(key)) {
            requestLoad(key, snapshot(page));
        }
        return Optional.empty();
    }

    synchronized void clear() {
        generation++;
        images.clear();
        pending.clear();
        failed.clear();
    }

    private static Page snapshot(Page page) {
        Page copy = new Page(page.getFile(), page.getIndex());
        copy.setRotation(page.getRotation());
        return copy;
    }

    private void requestLoad(Key key, Page page) {
        int requestGeneration = generation;
        loadExecutor.execute(() -> {
            Image image = null;
            try {
                image = loader.load(page, key.width(), key.height());
            } catch (IOException | RuntimeException e) {
                Logger.error(e, "Failed to render {}", page);
            }
            Image result = image;
            resultExecutor.execute(() -> store(key, result, requestGeneration));
        });
    }

    private void store(Key key, @Nullable Image image, int requestGeneration) {
        synchronized (this) {
            if (requestGeneration != generation) {
                return;
            }
            pending.remove(key);
            if (image == null) {
                failed.add(key);
                return;
            }
            images.put(key, image);
        }
        onLoaded.run();
    }
}
