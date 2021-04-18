package pl.pola_app.helpers;


import android.graphics.Bitmap;
import android.view.SurfaceHolder;

public abstract class CameraCompat {

    public static CameraCompat create(int minPicSize) {
        return new OldCamera(minPicSize);
    }

    public abstract void open();

    public abstract void openPreview(SurfaceHolder surfaceholder);

    public abstract void closePreview();

    public abstract void takePicture(OnPhotoTakenSuccessListener onPhotoTakenSuccessListener);

    public abstract void release();

    public interface OnPhotoTakenSuccessListener {
        void onPhotoTakenSuccess(Bitmap bitmap, int originalWidth, int originalHeight, int width, int height);
    }
}
