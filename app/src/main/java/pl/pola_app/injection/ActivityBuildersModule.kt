package pl.pola_app.injection

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.pola_app.feature.browser.BrowserActivity
import pl.pola_app.feature.digit.DigitActivity
import pl.pola_app.feature.main.MainActivity
import pl.pola_app.feature.menu.MenuActivity
import pl.pola_app.feature.splash.SplashActivity

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    abstract fun contributeMenuActivity(): MenuActivity

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    abstract fun contributeBrowserActivity(): BrowserActivity

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    abstract fun contributeDigitActivity(): DigitActivity

}