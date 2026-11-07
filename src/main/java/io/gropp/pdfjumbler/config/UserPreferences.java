package io.gropp.pdfjumbler.config;

import java.util.prefs.Preferences;

public class UserPreferences {
    private final Preferences preferences;

    public UserPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public static Preferences userNode() {
        return Preferences.userNodeForPackage(UserPreferences.class);
    }

    public static UserPreferences forCurrentUser() {
        return new UserPreferences(userNode());
    }

    public boolean isShowText() {
        return preferences.getBoolean(ConfigKeys.SHOW_TEXT, true);
    }

    public void setShowText(boolean showText) {
        preferences.putBoolean(ConfigKeys.SHOW_TEXT, showText);
    }
}
