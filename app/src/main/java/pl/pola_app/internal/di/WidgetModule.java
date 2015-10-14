package pl.pola_app.internal.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.pola_app.helpers.LinearLayoutManager;

@Module
public class WidgetModule {
    private final Context context;

    public WidgetModule(Context context) {
        this.context = context;
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager() {
        return new LinearLayoutManager(context);
    }
}
