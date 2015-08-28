package pl.pola_app.internal.di;

import android.content.Context;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;

@Module
public class SystemServicesModule {
    private final Context context;

    public SystemServicesModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Provides
    LayoutInflater provideLayoutInflater() {
        return LayoutInflater.from(context);
    }
}
