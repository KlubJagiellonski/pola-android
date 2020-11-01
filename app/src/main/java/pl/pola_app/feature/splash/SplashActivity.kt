package pl.pola_app.feature.splash

import android.content.Intent
import android.os.Handler
import androidx.activity.viewModels
import pl.pola_app.R
import pl.pola_app.databinding.ActivitySplashBinding
import pl.pola_app.feature.base.BaseActivity
import pl.pola_app.feature.main.MainActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    override val viewModel by viewModels<SplashViewModel> { viewModelFactory }

    private val SPLASH_DELAY: Long = 1500

    override fun initObservers() {

    }

    override fun initBaseData() {

    }

    override fun initListeners() {

    }

    override fun initView() {

        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, SPLASH_DELAY)

    }

    override fun initAnimations() {

    }
}