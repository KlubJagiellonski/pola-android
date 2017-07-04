package pl.pola_app.helpers;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.pola_app.internal.di.qualifier.SettingsPrefs;

@Singleton
public class SettingsPreference {

    private static final String SHOULD_DISPLAY_HELP_MSG_SCREEN = "SHOULD_DISPLAY_HELP_MSG_SCREEN";
    private static final String SHOULD_DISPLAY_HELP_MSG_DIALOG = "SHOULD_DISPLAY_HELP_MSG_DIALOG";

    private final SharedPreferences prefs;


    @Inject
    public SettingsPreference(@SettingsPrefs SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public boolean shouldDisplayHelpMessageScreen() {
        return prefs.getBoolean(SHOULD_DISPLAY_HELP_MSG_SCREEN, true);
    }

    public boolean shouldDisplayHelpMessageDialog() {
        return prefs.getBoolean(SHOULD_DISPLAY_HELP_MSG_DIALOG, false);
    }

    public void neverDisplayHelpMessageScreen() {
        prefs.edit().putBoolean(SHOULD_DISPLAY_HELP_MSG_SCREEN, false).apply();
    }

    public void neverDisplayHelpMessageDialog() {
        prefs.edit().putBoolean(SHOULD_DISPLAY_HELP_MSG_DIALOG, false).apply();

    }
}
