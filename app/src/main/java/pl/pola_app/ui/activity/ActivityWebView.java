package pl.pola_app.ui.activity;

import android.os.Bundle;
import android.webkit.WebChromeClient;

import androidx.appcompat.app.AppCompatActivity;

import pl.pola_app.databinding.ActivityAboutBinding;
import pl.pola_app.helpers.Utils;

public class ActivityWebView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url")) {
            binding.webView.loadUrl(getIntent().getExtras().getString("url"));
        } else {
            binding.webView.loadUrl(Utils.URL_POLA_ABOUT);
        }
        binding.webView.setWebChromeClient(new WebChromeClient());
    }
}
