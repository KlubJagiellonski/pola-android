package pl.pola_app.helpers;

import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

public class Utils {
    public static final String URL_POLA_ABOUT = "https://www.pola-app.pl/m/about";
    public static final String URL_POLA_METHOD = "https://www.pola-app.pl/m/method";
    public static final String URL_POLA_KJ = "https://www.pola-app.pl/m/kj";
    public static final String URL_POLA_TEAM = "https://www.pola-app.pl/m/team";
    public static final String URL_POLA_PARTNERS = "https://www.pola-app.pl/m/partners";
    public static final String POLA_MAIL = "kontakt@pola-app.pl";
    public static final String URL_POLA_GOOGLEPLAY = "https://play.google.com/store/apps/details?id=pl.pola_app";
    public static final String URL_POLA_FB = "https://www.facebook.com/app.pola";
    public static final String URL_POLA_TWITTER = "https://twitter.com/pola_app";
    public static final long TIMEOUT_SECONDS = 20;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getDeviceName() {
        if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
            return upperFirstLetter(Build.MODEL);
        }
        return "Android: " + upperFirstLetter(Build.MANUFACTURER) + " " + Build.MODEL;
    }

    private static String upperFirstLetter(String strToUpper) {
        if (TextUtils.isEmpty(strToUpper)) {
            return strToUpper;
        }
        char[] charArray = strToUpper.toCharArray();

        final StringBuilder sb = new StringBuilder();
        boolean upperNext = true;
        for (char singleChar : charArray) {
            if (upperNext && Character.isLetter(singleChar)) {
                singleChar = Character.toUpperCase(singleChar);
                upperNext = false;
            }else if (Character.isWhitespace(singleChar)) {
                upperNext = true;
            }
            sb.append(singleChar);
        }

        return sb.toString();
    }
}
