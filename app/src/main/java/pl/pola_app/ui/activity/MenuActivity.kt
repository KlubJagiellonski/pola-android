package pl.pola_app.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import pl.pola_app.BuildConfig
import pl.pola_app.R
import pl.pola_app.helpers.*
import pl.pola_app.helpers.SessionId.Companion.create
import pl.pola_app.ui.activity.CreateReportActivity

class MenuActivity : AppCompatActivity() {
    @kotlin.jvm.JvmField
    @BindView(R.id.app_build_tv)
    var appBuildTv: TextView? = null
    private var logger: EventLogger? = null
    private lateinit var sessionId: SessionId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        ButterKnife.bind(this, this)
        logger = EventLogger(this)
        sessionId = create(this)
        setupView()
    }

    private fun setupView() {
        appBuildTv?.text = getString(
            R.string.pola_application,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
    }

    @OnClick(R.id.menu_back_iv)
    fun onMenuBackClick() {
        finish()
    }

    @OnClick(R.id.activity_menu_about_app_tv)
    fun onAboutAppClick() {
        logger?.logMenuItemOpened("O Aplikacji Pola", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_ABOUT)
        startActivity(intent)
    }

    @OnClick(R.id.activity_menu_user_manual_tv)
    fun onUserManualClick() {
        logger?.logMenuItemOpened("Instrukcja obsługi", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_METHOD)
        startActivity(intent)
    }

    @OnClick(R.id.activity_menu_about_kj_tv)
    fun onAboutKJClick() {
        logger?.logMenuItemOpened("O Klubie Jagiellońskim", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_KJ)
        startActivity(intent)
    }

    @OnClick(R.id.activity_menu_team_tv)
    fun onTeamClick() {
        logger?.logMenuItemOpened("Zespół", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_TEAM)
        startActivity(intent)
    }

    @OnClick(R.id.activity_menu_partners_tv)
    fun onPartnersClick() {
        logger?.logMenuItemOpened("Partnerzy", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_PARTNERS)
        startActivity(intent)
    }

    @OnClick(R.id.activity_menu_friends_tv)
    fun onFriendsClick() {
        logger?.logMenuItemOpened("Przyjaciele Poli", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_FRIENDS)
        startActivity(intent)
    }

    @OnClick(R.id.activity_found_bug_tv)
    fun onFoundBuglick() {
        logger?.logMenuItemOpened("Zgłoś błąd w danych", sessionId.get())
        val intent = Intent(this, CreateReportActivity::class.java)
        intent.action = "product_report"
        startActivity(intent)
    }

    @OnClick(R.id.activity_rate_tv)
    fun onRateClick() {
        logger?.logMenuItemOpened("Pola na Twitterze", sessionId.get())
        val uri = Uri.parse("market://details?id=" + this.packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
                )
            )
        }
    }

    @OnClick(R.id.activity_facebook_tv)
    fun onFacebookClick() {
        logger?.logMenuItemOpened("Pola na Facebooku", sessionId.get())
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URL_POLA_FB)))
    }

    @OnClick(R.id.activity_twitter_tv)
    fun onTwitterClick() {
        logger?.logMenuItemOpened("Pola na Twitterze", sessionId.get())
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URL_POLA_TWITTER)))
    }
}