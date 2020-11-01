package pl.pola_app.feature.menu

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import pl.pola_app.BuildConfig
import pl.pola_app.R
import pl.pola_app.databinding.ActivityMenuBinding
import pl.pola_app.feature.base.BaseActivity

class MenuActivity : BaseActivity<ActivityMenuBinding>(R.layout.activity_menu) {

    override val viewModel by viewModels<MenuViewModel> { viewModelFactory }

    override fun initObservers() {

    }

    override fun initBaseData() {

    }

    override fun initListeners() {

    }

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }

        binding.appBuildTv.setText(
            getString(
                R.string.pola_application,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing, R.anim.slide_out)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, MenuActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing)
        }
    }

    override fun initAnimations() {

    }
}