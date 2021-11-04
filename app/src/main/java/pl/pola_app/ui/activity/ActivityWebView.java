package pl.pola_app.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebChromeClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import pl.pola_app.databinding.ActivityAboutBinding;

public class ActivityWebView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
        //https://developer.android.com/guide/webapps/dark-theme#daynight

         */
        if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    WebSettingsCompat.setForceDark(binding.webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    WebSettingsCompat.setForceDark(binding.webView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                    break;
            }
        }

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url")) {
            binding.webView.loadUrl(getIntent().getExtras().getString("url"));
        } else {
            binding.webView.loadUrl("https://www.pola-app.pl/m/about");
//            binding.webView.loadUrl(Utils.URL_POLA_ABOUT);
        }
        binding.webView.setWebChromeClient(new WebChromeClient());
    }
}
