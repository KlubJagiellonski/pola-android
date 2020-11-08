package pl.pola_app.feature.details

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import pl.pola_app.R
import pl.pola_app.databinding.ActivityDetailsBinding
import pl.pola_app.feature.base.BaseActivity
import pl.pola_app.repository.SearchResult

class DetailsActivity : BaseActivity<ActivityDetailsBinding>(R.layout.activity_details) {

    override val viewModel by viewModels<DetailsViewModel> { viewModelFactory }

    override fun initObservers() {

    }

    override fun initBaseData() {
        intent.getSerializableExtra(SEARCH_RESULT)?.let {
            (it as? SearchResult)?.let {
                viewModel.searchResult.value = it
                supportActionBar?.title = it.name
            }
        }
    }

    override fun initListeners() {

    }

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
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
        private const val SEARCH_RESULT = "SEARCH_RESULT"

        fun start(activity: Activity, searchResult: SearchResult) {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra(SEARCH_RESULT, searchResult)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing)
        }
    }

    override fun initAnimations() {

    }
}