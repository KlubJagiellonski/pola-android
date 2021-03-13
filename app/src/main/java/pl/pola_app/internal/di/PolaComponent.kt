package pl.pola_app.internal.di

import dagger.Component
import pl.pola_app.PolaApplication
import pl.pola_app.ui.activity.MainActivity
import pl.pola_app.ui.fragment.ProductDetailsFragment
import pl.pola_app.ui.fragment.ScannerFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [OttoModule::class, WidgetModule::class, SharedPrefsModule::class])
interface PolaComponent {
    object Initializer {
        @JvmStatic
        fun init(app: PolaApplication): PolaComponent {
            return DaggerPolaComponent.builder()
                .widgetModule(WidgetModule(app))
                .sharedPrefsModule(SharedPrefsModule(app))
                .build()
        }
    }

    fun inject(mainActivity: MainActivity?)
    fun inject(scannerFragment: ScannerFragment)
    fun inject(productDetailsFragment: ProductDetailsFragment)
}