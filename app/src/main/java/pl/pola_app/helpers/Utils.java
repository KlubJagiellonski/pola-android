package pl.pola_app.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

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
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String fileToSend(String path) {
        if(path == null) {
            return null;
        }
        Bitmap decoded = BitmapFactory.decodeFile(path);
        if(decoded == null) {
            return null;
        }
        decoded = resizeToHd(decoded);
        return Base64.encodeToString(getBytesFromBitmap(decoded), Base64.NO_WRAP);
    }

    public static Bitmap resizeToHd(Bitmap bitmap) {
        if(bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
            float aspectRatio = bitmap.getWidth() / (float) bitmap.getHeight();
            int width = 1000;
            int height = Math.round(width / aspectRatio);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        }
        return bitmap;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}
