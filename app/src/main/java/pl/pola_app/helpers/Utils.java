package pl.pola_app.helpers;

import android.content.Context;
import android.provider.Settings;

public class Utils {
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
