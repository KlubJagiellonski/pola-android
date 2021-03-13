package pl.pola_app.internal.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import pl.pola_app.internal.di.qualifier.SettingsPrefs

@Module
class SharedPrefsModule internal constructor(private val context: Context) {
    @Provides
    @SettingsPrefs
    fun provideSettingsPrefs(): SharedPreferences {
        return context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    }

    companion object {
        private const val SETTINGS_PREFS = "SETTINGS_PREFS"
    }
}