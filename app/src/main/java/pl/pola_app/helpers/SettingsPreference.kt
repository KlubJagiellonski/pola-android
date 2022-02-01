package pl.pola_app.helpers

import javax.inject.Singleton
import javax.inject.Inject
import pl.pola_app.internal.di.qualifier.SettingsPrefs
import android.content.SharedPreferences
import pl.pola_app.helpers.SettingsPreference

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

    fun increaseSuccessCounter(){
        val successNumber = getSuccessScanCounter()
        prefs.edit().putInt(SUCCESS_SCAN_COUNTER, successNumber+1).apply()
    }

    fun getSuccessScanCounter():Int{
        return prefs.getInt(SUCCESS_SCAN_COUNTER, 0)
    }

    companion object {
        private const val SHOULD_DISPLAY_HELP_MSG_SCREEN = "SHOULD_DISPLAY_HELP_MSG_SCREEN"
        private const val SHOULD_DISPLAY_HELP_MSG_DIALOG = "SHOULD_DISPLAY_HELP_MSG_DIALOG"
        private const val SUCCESS_SCAN_COUNTER = "SUCCESS_SCAN_COUNTER"
    }
}