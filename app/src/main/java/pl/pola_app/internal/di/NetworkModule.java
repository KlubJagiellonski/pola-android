package pl.pola_app.internal.di;


import com.octo.android.robospice.SpiceManager;

import dagger.Module;
import dagger.Provides;
import pl.pola_app.network.PolaSpiceService;

@Module
public class NetworkModule {

    @Provides
    SpiceManager provideSpiceManager() {
        return new SpiceManager(PolaSpiceService.class);
    }
}
