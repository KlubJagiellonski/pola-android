package pl.pola_app.internal.di;

import android.content.Context;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }
}
