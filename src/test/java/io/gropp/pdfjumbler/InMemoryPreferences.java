package io.gropp.pdfjumbler;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import org.jspecify.annotations.Nullable;

public final class InMemoryPreferences extends AbstractPreferences {
    private final Map<String, String> values = new HashMap<>();
    private final Map<String, InMemoryPreferences> children = new HashMap<>();

    public InMemoryPreferences() {
        this(null, "");
    }

    private InMemoryPreferences(@Nullable InMemoryPreferences parent, String name) {
        super(parent, name);
    }

    @Override
    protected void putSpi(String key, String value) {
        values.put(key, value);
    }

    @Override
    protected @Nullable String getSpi(String key) {
        return values.get(key);
    }

    @Override
    protected void removeSpi(String key) {
        values.remove(key);
    }

    @Override
    protected void removeNodeSpi() {}

    @Override
    protected String[] keysSpi() {
        return values.keySet().toArray(String[]::new);
    }

    @Override
    protected String[] childrenNamesSpi() {
        return children.keySet().toArray(String[]::new);
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        return children.computeIfAbsent(name, childName -> new InMemoryPreferences(this, childName));
    }

    @Override
    protected void syncSpi() {}

    @Override
    protected void flushSpi() {}
}
