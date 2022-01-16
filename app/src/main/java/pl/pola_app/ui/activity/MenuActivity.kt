package pl.pola_app.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.pola_app.BuildConfig
import pl.pola_app.R.string
import pl.pola_app.databinding.ActivityMenuBinding
import pl.pola_app.helpers.EventLogger
import pl.pola_app.helpers.SessionId
import pl.pola_app.helpers.Utils

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var logger: EventLogger
    private lateinit var sessionId: SessionId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logger = EventLogger(this)
        sessionId = SessionId.create(this)
        setupView()
        with(binding) {
            menuBackIv.setOnClickListener { onMenuBackClick() }
            activityMenuAboutAppTv.setOnClickListener { onAboutAppClick() }
            activityMenuUserManualTv.setOnClickListener { onUserManualClick() }
            activityMenuAboutKjTv.setOnClickListener { onAboutKlubJagiellonskiClick() }
            activityMenuTeamTv.setOnClickListener { onTeamClick() }
            activityMenuPartnersTv.setOnClickListener { onPartnersClick() }
            activityMenuFriendsTv.setOnClickListener { onFriendsClick() }
            activityFoundBugTv.setOnClickListener { onFoundBugClick() }
            activityRateTv.setOnClickListener { onRateClick() }
            activityGhTv.setOnClickListener { onGhClick() }
            activityFacebookTv.setOnClickListener { onFacebookClick() }
            activityTwitterTv.setOnClickListener { onTwitterClick() }
        }
    }

    private fun setupView() {
        binding.appBuildTv.text = getString(string.pola_application, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

    private fun onMenuBackClick() {
        finish()
    }

    private fun onAboutAppClick() {
        logger.logMenuItemOpened("O Aplikacji Pola", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", Utils.URL_POLA_ABOUT)
        startActivity(intent)
    }

    private fun onUserManualClick() {
        logger.logMenuItemOpened("Instrukcja obsługi", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", Utils.URL_POLA_METHOD)
        startActivity(intent)
    }

    private fun onAboutKlubJagiellonskiClick() {
        logger.logMenuItemOpened("O Klubie Jagiellońskim", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", Utils.URL_POLA_KJ)
        startActivity(intent)
    }

    private fun onTeamClick() {
        logger.logMenuItemOpened("Zespół", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", Utils.URL_POLA_TEAM)
        startActivity(intent)
    }

    private fun onPartnersClick() {
        logger.logMenuItemOpened("Partnerzy", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", Utils.URL_POLA_PARTNERS)
        startActivity(intent)
    }

    private fun onFriendsClick() {
        logger.logMenuItemOpened("Przyjaciele Poli", sessionId.get())
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", Utils.URL_POLA_FRIENDS)
        startActivity(intent)
    }

    private fun onFoundBugClick() {
        logger.logMenuItemOpened("Zgłoś błąd w danych", sessionId.get())
        val intent = Intent(this, CreateReportActivity::class.java)
        intent.action = "product_report"
        startActivity(intent)
    }

    private fun onRateClick() {
        logger.logMenuItemOpened("Pola na Twitterze", sessionId.get())
        val uri = Uri.parse("market://details?id=" + this.packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
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

    private fun onGhClick() {
        logger.logMenuItemOpened("Github", sessionId.get())
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_GH)))
    }

    private fun onFacebookClick() {
        logger.logMenuItemOpened("Pola na Facebooku", sessionId.get())
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_FB)))
    }

    private fun onTwitterClick() {
        logger.logMenuItemOpened("Pola na Twitterze", sessionId.get())
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_TWITTER)))
    }
}
