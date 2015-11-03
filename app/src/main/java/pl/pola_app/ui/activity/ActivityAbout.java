package pl.pola_app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.R;

public class ActivityAbout extends Activity {
    @Bind(R.id.web_view)
    WebView webView;

    private static final String URL_POLA_ABOUT = "https://www.pola-app.pl/m/about";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl(URL_POLA_ABOUT);
        webView.setWebChromeClient(new WebChromeClient());
    }
}
