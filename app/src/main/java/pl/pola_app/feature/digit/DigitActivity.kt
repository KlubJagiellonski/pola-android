package pl.pola_app.feature.digit

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import pl.pola_app.R
import pl.pola_app.databinding.ActivityDigitBinding
import pl.pola_app.feature.base.BaseActivity

class DigitActivity : BaseActivity<ActivityDigitBinding>(R.layout.activity_digit) {

    override val viewModel by viewModels<DigitViewModel> { viewModelFactory }

    override fun initObservers() {

    }

    override fun initBaseData() {

    }

    override fun initListeners() {

    }

    override fun initView() {
        binding.viewModel = viewModel
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing, R.anim.slide_out)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun initAnimations() {

    }


    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, DigitActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing)
        }
    }
}