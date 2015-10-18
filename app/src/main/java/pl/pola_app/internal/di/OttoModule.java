package pl.pola_app.internal.di;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class OttoModule {

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }
}