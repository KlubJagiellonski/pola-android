package pl.pola_app.helpers;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

@TargetApi(21)
public class OldCamera extends CameraCompat {

    private static final int DEFAULT_MIN_PIC_SIZE = 1000;
    Camera camera;
    boolean safeToTakePicture = false;
    boolean isPreviewing = false;

    private final int maxPictureSize;

    OldCamera(int maxPictureSize) {
        this.maxPictureSize = maxPictureSize;

    }

    @Override
    public void open(){
        camera = Camera.open();
        camera.cancelAutoFocus();
        final Camera.Parameters parameters = camera.getParameters();
        final List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = supportedPictureSizes.get(0);
        for (Camera.Size cameraSize : supportedPictureSizes) {

            if ((size.width > cameraSize.width && cameraSize.width > maxPictureSize && cameraSize.height > maxPictureSize)
                    || (size.height < maxPictureSize || size.width < maxPictureSize)) {
                size = cameraSize;
            }
        }

        parameters.setPictureSize(size.width, size.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
    }


    @Override
    public void openPreview(SurfaceHolder surface) {
        try {
            camera.setPreviewDisplay(surface);
            camera.startPreview();
            camera.autoFocus(null);
            isPreviewing = true;
            safeToTakePicture = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closePreview() {
        camera.stopPreview();
        camera.release();
        isPreviewing = false;
        safeToTakePicture = false;
    }

    @Override
    public void takePicture(OnPhotoTakenSuccessListener onPhotoTakenSuccessListener) {
        if (!safeToTakePicture) {
            Log.e("TAG", "Skipped photo");
            return;
        }
        camera.takePicture(null, null, (data, camera) -> {
            safeToTakePicture = true;
            final Camera.Parameters parameters = camera.getParameters();
            final Camera.Size pictureSize = parameters.getPictureSize();
            final Bitmap bitmap = FileUtils.rotateImageAndScale(BitmapFactory.decodeByteArray(data, 0, data.length), 90, maxPictureSize);
            onPhotoTakenSuccessListener.onPhotoTakenSuccess(
                    bitmap,
                    pictureSize.width,
                    pictureSize.height,
                    bitmap.getWidth(),
                    bitmap.getHeight()

            );
            camera.startPreview();
        });
        safeToTakePicture = false;
    }
}
