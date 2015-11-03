package pl.pola_app.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Utils {
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
