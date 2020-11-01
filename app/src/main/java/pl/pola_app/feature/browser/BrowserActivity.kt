package pl.pola_app.feature.browser

import android.app.Activity
import android.content.Intent
import android.webkit.WebChromeClient
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import pl.pola_app.R
import pl.pola_app.databinding.ActivityBrowserBinding
import pl.pola_app.feature.base.BaseActivity

class BrowserActivity : BaseActivity<ActivityBrowserBinding>(R.layout.activity_browser) {

    override val viewModel by viewModels<BrowserViewModel> { viewModelFactory }

    override fun initObservers() {
        viewModel.url.observe(this, Observer {
            binding.webView.loadUrl(it)
        })
    }

    override fun initBaseData() {
        intent.getStringExtra(EXTRA_URL)?.let {
            viewModel.url.value = it
        }
    }

    override fun initListeners() {

    }

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
            title = ""
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.webChromeClient = WebChromeClient()
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

        const val EXTRA_URL = "EXTRA_URL"

        fun start(activity: Activity, url: String) {
            val intent = Intent(activity, BrowserActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing)
        }
    }
}