package pl.pola_app.internal.di;


import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;
import pl.pola_app.internal.di.qualifier.SettingsPrefs;

@Module
public class SharedPrefsModule {

    private static final String SETTINGS_PREFS = "SETTINGS_PREFS";

    private final Context context;

    SharedPrefsModule(Context context) {
        this.context = context;
    }

    @Provides
    @SettingsPrefs
    SharedPreferences provideSettingsPrefs() {
        return context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE);
    }
}
