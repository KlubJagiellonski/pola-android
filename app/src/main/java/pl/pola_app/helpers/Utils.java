package pl.pola_app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class Utils {
    public static final String URL_POLA_ABOUT = "https://www.pola-app.pl/m/about";
    public static final String URL_POLA_METHOD = "https://www.pola-app.pl/m/method";
    public static final String URL_POLA_KJ = "https://www.pola-app.pl/m/kj";
    public static final String URL_POLA_TEAM= "https://www.pola-app.pl/m/team";
    public static final String URL_POLA_PARTNERS = "https://www.pola-app.pl/m/partners";
    public static final String POLA_MAIL = "kontakt@pola-app.pl";
    public static final String URL_POLA_GOOGLEPLAY= "https://play.google.com/store/apps/details?id=pl.pola_app";
    public static final String URL_POLA_FB = "https://www.facebook.com/app.pola";
    public static final String URL_POLA_TWITTER= "https://twitter.com/pola_app";
    public static final long TIMEOUT_SECONDS = 20;

    private static final String PREF_SESSION_GUID = "session_guid";

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getSessionGuid(Context context) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        String sessionGuid = pref.getString(PREF_SESSION_GUID, null);

        if(sessionGuid == null) {
            sessionGuid = UUID.randomUUID().toString();
            final SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREF_SESSION_GUID, sessionGuid);
            editor.commit();
        }

        return sessionGuid;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
