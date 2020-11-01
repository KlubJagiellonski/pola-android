package pl.pola_app

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import pl.pola_app.injection.DaggerApplicationComponent

class PolaApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }

}