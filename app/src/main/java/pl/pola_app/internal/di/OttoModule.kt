package pl.pola_app.internal.di

import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class OttoModule {
    @Provides
    @Singleton
    fun provideBus(): Bus {
        return Bus()
    }
}