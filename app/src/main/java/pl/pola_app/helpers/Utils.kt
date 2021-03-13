package pl.pola_app.helpers

import android.content.res.Resources
import android.os.Build
import pl.pola_app.BuildConfig
import java.util.*


const val URL_POLA_ABOUT = "https://www.pola-app.pl/m/about"
const val URL_POLA_METHOD = "https://www.pola-app.pl/m/method"
const val URL_POLA_KJ = "https://www.pola-app.pl/m/kj"
const val URL_POLA_TEAM = "https://www.pola-app.pl/m/team"
const val URL_POLA_PARTNERS = "https://www.pola-app.pl/m/partners"
const val URL_POLA_FRIENDS = "https://www.pola-app.pl/m/friends"
const val POLA_MAIL = "pola@klubjagiellonski.pl"
const val URL_POLA_GOOGLEPLAY = "https://play.google.com/store/apps/details?id=pl.pola_app"
const val URL_POLA_FB = "https://www.facebook.com/app.pola"
const val URL_POLA_TWITTER = "https://twitter.com/pola_app"
const val TIMEOUT_SECONDS: Long = 20

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Int.pxToDp(): Int {
    return (this / Resources.getSystem().displayMetrics.density).toInt()
}

val deviceName: String
    get() = if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
        Build.MODEL.upperFirstLetter()
    } else "Android: " + Build.MANUFACTURER.upperFirstLetter() + " " + Build.MODEL + " (" + BuildConfig.VERSION_NAME + ")"

private fun String.upperFirstLetter(): String =
    split(" ").joinToString(" ") { it.capitalize(Locale.getDefault()) }
