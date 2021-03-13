package pl.pola_app.helpers

import android.content.SharedPreferences
import pl.pola_app.internal.di.qualifier.SettingsPrefs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreference @Inject constructor(@param:SettingsPrefs private val prefs: SharedPreferences) {
    fun shouldDisplayHelpMessageScreen(): Boolean {
        return prefs.getBoolean(SHOULD_DISPLAY_HELP_MSG_SCREEN, true)
    }

    fun shouldDisplayHelpMessageDialog(): Boolean {
        return prefs.getBoolean(SHOULD_DISPLAY_HELP_MSG_DIALOG, false)
    }

    fun neverDisplayHelpMessageScreen() {
        prefs.edit().putBoolean(SHOULD_DISPLAY_HELP_MSG_SCREEN, false).apply()
    }

    fun neverDisplayHelpMessageDialog() {
        prefs.edit().putBoolean(SHOULD_DISPLAY_HELP_MSG_DIALOG, false).apply()
    }

    companion object {
        private const val SHOULD_DISPLAY_HELP_MSG_SCREEN = "SHOULD_DISPLAY_HELP_MSG_SCREEN"
        private const val SHOULD_DISPLAY_HELP_MSG_DIALOG = "SHOULD_DISPLAY_HELP_MSG_DIALOG"
    }
}