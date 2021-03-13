package pl.pola_app.ui.activity

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import pl.pola_app.R
import pl.pola_app.helpers.URL_POLA_ABOUT

class ActivityWebView : AppCompatActivity() {
    @kotlin.jvm.JvmField
    @BindView(R.id.web_view)
    var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ButterKnife.bind(this)
        webView?.run {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            if (intent != null && intent.extras != null && intent.extras?.containsKey("url") == true) {
                loadUrl(intent.extras?.getString("url"))
            } else {
                loadUrl(URL_POLA_ABOUT)
            }
            webChromeClient = WebChromeClient()
        }

    }
}