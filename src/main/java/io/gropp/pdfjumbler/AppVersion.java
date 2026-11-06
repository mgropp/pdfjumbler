package io.gropp.pdfjumbler;

import org.jspecify.annotations.Nullable;

public final class AppVersion {
    static final String DEVELOPMENT = "dev";

    private AppVersion() {}

    public static String current() {
        return orDevelopment(AppVersion.class.getPackage().getImplementationVersion());
    }

    static String orDevelopment(@Nullable String implementationVersion) {
        if (implementationVersion == null || implementationVersion.isBlank()) {
            return DEVELOPMENT;
        }
        return implementationVersion;
    }
}
