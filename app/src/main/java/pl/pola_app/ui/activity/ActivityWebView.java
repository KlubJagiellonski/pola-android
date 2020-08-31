package pl.pola_app.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url")) {
            webView.loadUrl(getIntent().getExtras().getString("url"));
        } else {
            webView.loadUrl(Utils.URL_POLA_ABOUT);
        }
        webView.setWebChromeClient(new WebChromeClient());
    }
}
