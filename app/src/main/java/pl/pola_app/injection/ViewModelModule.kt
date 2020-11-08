package pl.pola_app.injection

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import pl.pola_app.PolaApp
import pl.pola_app.feature.browser.BrowserViewModel
import pl.pola_app.feature.details.DetailsViewModel
import pl.pola_app.feature.digit.DigitViewModel
import pl.pola_app.feature.main.MainViewModel
import pl.pola_app.feature.menu.MenuViewModel
import pl.pola_app.feature.splash.SplashViewModel

@Module
abstract class ViewModelModule(private val app: PolaApp) {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(viewmodel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MenuViewModel::class)
    internal abstract fun bindMenuViewModel(viewmodel: MenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun bindSplashViewModel(viewmodel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BrowserViewModel::class)
    internal abstract fun bindBrowserViewModel(viewmodel: BrowserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DigitViewModel::class)
    internal abstract fun bindDigitViewModel(viewmodel: DigitViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    internal abstract fun bindDetailsViewModel(viewmodel: DetailsViewModel): ViewModel
}