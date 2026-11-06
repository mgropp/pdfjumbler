package io.gropp.pdfjumbler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AppVersionTest {
    @Test
    void usesImplementationVersionWhenPresent() {
        assertThat(AppVersion.orDevelopment("0.1.0")).isEqualTo("0.1.0");
    }

    @Test
    void fallsBackToDevelopmentWithoutVersion() {
        assertThat(AppVersion.orDevelopment(null)).isEqualTo("dev");
        assertThat(AppVersion.orDevelopment(" ")).isEqualTo("dev");
    }

    @Test
    void isDevelopmentWhenNotRunningFromReleaseJar() {
        assertThat(AppVersion.current()).isEqualTo("dev");
    }
}
