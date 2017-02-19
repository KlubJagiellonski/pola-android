package pl.pola_app.internal.di;

import android.content.Context;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;

@Module
public class WidgetModule {
    private final Context context;

    public WidgetModule(Context context) {
        this.context = context;
    }

    @Provides
    Resources provideResources() {
        return context.getResources();
    }
}
