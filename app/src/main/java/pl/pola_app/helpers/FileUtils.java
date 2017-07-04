package pl.pola_app.helpers;


import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static void saveBitmap(Bitmap bitmap, String picturePath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap rotateImageAndScale(Bitmap source, float angle, int maxSize) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        final int width = source.getWidth();
        final int height = source.getHeight();
        final Bitmap sourceBitmap = maxSize >= width && maxSize >= height ? source
                : width > height ? Bitmap.createScaledBitmap(source, maxSize, height * maxSize / width, true)
                : Bitmap.createScaledBitmap(source, width * maxSize / height, maxSize, true);
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
    }
}
