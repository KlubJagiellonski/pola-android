package pl.pola_app.injection

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import pl.pola_app.PolaApp
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ViewModelModule::class,
        RepositoryModule::class,
        ViewModelBuilder::class,
        ActivityBuildersModule::class,
        AndroidSupportInjectionModule::class]
)
interface ApplicationComponent : AndroidInjector<PolaApp> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}