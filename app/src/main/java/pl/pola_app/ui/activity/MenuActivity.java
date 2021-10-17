package pl.pola_app.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import pl.pola_app.BuildConfig;
import pl.pola_app.R;
import pl.pola_app.databinding.ActivityMenuBinding;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.Utils;

public class MenuActivity extends AppCompatActivity {

    private ActivityMenuBinding binding;

    private EventLogger logger;
    private SessionId sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        logger = new EventLogger(this);
        sessionId = SessionId.create(this);
        setupView();
        binding.menuBackIv.setOnClickListener(this::onMenuBackClick);
        binding.activityMenuAboutAppTv.setOnClickListener(this::onAboutAppClick);
        binding.activityMenuUserManualTv.setOnClickListener(this::onUserManualClick);
        binding.activityMenuAboutKjTv.setOnClickListener(this::onAboutKJClick);
        binding.activityMenuTeamTv.setOnClickListener(this::onTeamClick);
        binding.activityMenuPartnersTv.setOnClickListener(this::onPartnersClick);
        binding.activityMenuFriendsTv.setOnClickListener(this::onFriendsClick);
        binding.activityFoundBugTv.setOnClickListener(this::onFoundBugClick);
        binding.activityRateTv.setOnClickListener(this::onRateClick);
        binding.activityFacebookTv.setOnClickListener(this::onFacebookClick);
        binding.activityTwitterTv.setOnClickListener(this::onTwitterClick);
    }

    private void setupView() {
        binding.appBuildTv.setText(getString(R.string.pola_application, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    void onMenuBackClick(View view) {
        finish();
    }

    void onAboutAppClick(View view) {
        logger.logMenuItemOpened("O Aplikacji Pola", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_ABOUT);
        startActivity(intent);
    }

    void onUserManualClick(View view) {
        logger.logMenuItemOpened("Instrukcja obsługi", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_METHOD);
        startActivity(intent);
    }

    void onAboutKJClick(View view) {
        logger.logMenuItemOpened("O Klubie Jagiellońskim", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_KJ);
        startActivity(intent);
    }

    void onTeamClick(View view) {
        logger.logMenuItemOpened("Zespół", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_TEAM);
        startActivity(intent);
    }

    void onPartnersClick(View view) {
        logger.logMenuItemOpened("Partnerzy", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_PARTNERS);
        startActivity(intent);
    }

    void onFriendsClick(View view) {
        logger.logMenuItemOpened("Przyjaciele Poli", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_FRIENDS);
        startActivity(intent);
    }

    void onFoundBugClick(View view) {
        logger.logMenuItemOpened("Zgłoś błąd w danych", sessionId.get());
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.setAction("product_report");
        startActivity(intent);
    }

    void onRateClick(View view) {
        logger.logMenuItemOpened("Pola na Twitterze", sessionId.get());
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    void onFacebookClick(View view) {
        logger.logMenuItemOpened("Pola na Facebooku", sessionId.get());
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_FB)));
    }

    void onTwitterClick(View view) {
        logger.logMenuItemOpened("Pola na Twitterze", sessionId.get());
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_TWITTER)));
    }
}
