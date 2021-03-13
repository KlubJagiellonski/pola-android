package pl.pola_app.internal.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides

@Module
class WidgetModule(private val context: Context) {
    @Provides
    fun provideResources(): Resources {
        return context.resources
    }
}