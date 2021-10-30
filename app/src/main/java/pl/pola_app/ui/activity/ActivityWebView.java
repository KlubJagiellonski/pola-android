package pl.pola_app.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;

public class ActivityWebView extends AppCompatActivity {
    @BindView(R.id.web_view)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        /*
        //https://developer.android.com/guide/webapps/dark-theme#daynight

         */
        if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                    break;
            }
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url")) {
            webView.loadUrl(getIntent().getExtras().getString("url"));
        } else {
            webView.loadUrl(Utils.URL_POLA_ABOUT);
        }
        webView.setWebChromeClient(new WebChromeClient());
    }
}
