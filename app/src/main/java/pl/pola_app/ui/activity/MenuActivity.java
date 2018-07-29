package pl.pola_app.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.BuildConfig;
import pl.pola_app.R;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.Utils;

/**
 * Created by Rafał Gawlik on 30.08.17.
 */

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.app_build_tv) TextView appBuildTv;

    private EventLogger logger;
    private SessionId sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this, this);

        logger = new EventLogger(this);
        sessionId = SessionId.create(this);
        setupView();
    }

    private void setupView() {
        appBuildTv.setText(getString(R.string.pola_application, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    @OnClick(R.id.menu_back_iv)
    void onMenuBackClick(){
        finish();
    }

    @OnClick(R.id.activity_menu_about_app_tv)
    void onAboutAppClick() {
        logger.logMenuItemOpened("O Aplikacji Pola", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_ABOUT);
        startActivity(intent);
    }

    @OnClick(R.id.activity_menu_user_manual_tv)
    void onUserManualClick() {
        logger.logMenuItemOpened("Instrukcja obsługi", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_METHOD);
        startActivity(intent);
    }

    @OnClick(R.id.activity_menu_about_kj_tv)
    void onAboutKJClick() {
        logger.logMenuItemOpened("O Klubie Jagiellońskim", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_KJ);
        startActivity(intent);
    }

    @OnClick(R.id.activity_menu_team_tv)
    void onTeamClick() {
        logger.logMenuItemOpened("Zespół", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_TEAM);
        startActivity(intent);
    }

    @OnClick(R.id.activity_menu_partners_tv)
    void onPartnersClick() {
        logger.logMenuItemOpened("Partnerzy", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_PARTNERS);
        startActivity(intent);
    }

    @OnClick(R.id.activity_menu_friends_tv)
    void onFriendsClick() {
        logger.logMenuItemOpened("Przyjaciele Poli", sessionId.get());
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_FRIENDS);
        startActivity(intent);
    }

    @OnClick(R.id.activity_found_bug_tv)
    void onFoundBuglick() {
        logger.logMenuItemOpened("Zgłoś błąd w danych", sessionId.get());
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.setAction("product_report");
        startActivity(intent);
    }

    @OnClick(R.id.activity_rate_tv)
    void onRateClick() {
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

    @OnClick(R.id.activity_facebook_tv)
    void onFacebookClick() {
        logger.logMenuItemOpened("Pola na Facebooku", sessionId.get());
        startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_FB)));

    }

    @OnClick(R.id.activity_twitter_tv)
    void onTwitterClick() {
        logger.logMenuItemOpened("Pola na Twitterze", sessionId.get());
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_TWITTER)));
    }
}
