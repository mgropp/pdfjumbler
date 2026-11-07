package io.gropp.pdfjumbler.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.gropp.pdfjumbler.testutil.InMemoryPreferences;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.Test;

class UserPreferencesTest {
    private final Preferences preferences = new InMemoryPreferences();
    private final UserPreferences userPreferences = new UserPreferences(preferences);

    @Test
    void showsTextByDefault() {
        assertThat(userPreferences.isShowText()).isTrue();
    }

    @Test
    void storesShowText() {
        userPreferences.setShowText(false);

        assertThat(userPreferences.isShowText()).isFalse();
        assertThat(new UserPreferences(preferences).isShowText()).isFalse();
    }

    @Test
    void storesShowTextUnderTheConfiguredKey() {
        userPreferences.setShowText(false);

        assertThat(preferences.getBoolean(ConfigKeys.SHOW_TEXT, true)).isFalse();
    }
}
