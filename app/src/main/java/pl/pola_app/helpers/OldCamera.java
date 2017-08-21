package pl.pola_app.helpers;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import timber.log.Timber;

@TargetApi(21)
public class OldCamera extends CameraCompat {
    Camera camera;
    boolean safeToTakePicture = false;
    boolean isPreviewing = false;

    private final int maxPictureSize;

    OldCamera(int maxPictureSize) {
        this.maxPictureSize = maxPictureSize;

    }

    @Override
    public void open() {
        camera = Camera.open();
        camera.cancelAutoFocus();
        camera.setDisplayOrientation(90);
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
        parameters.setPreviewSize(size.width, size.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setRecordingHint(true);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
    }


    @Override
    public void openPreview(SurfaceHolder surface) {
        try {
            camera.setPreviewDisplay(surface);
            camera.startPreview();
            isPreviewing = true;
            safeToTakePicture = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closePreview() {
        isPreviewing = false;
        safeToTakePicture = false;
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void takePicture(OnPhotoTakenSuccessListener onPhotoTakenSuccessListener) {
        if (!safeToTakePicture) {
            Timber.e("Skipped photo");
            return;
        }
        camera.setOneShotPreviewCallback((data, camera1) -> {
            if (data != null && data.length != 0) {
                final Camera.Parameters parameters = camera1.getParameters();
                final Camera.Size pictureSize = parameters.getPictureSize();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, pictureSize.width, pictureSize.height, null);
                yuvImage.compressToJpeg(new Rect(0, 0, pictureSize.width, pictureSize.height), 100, out);
                byte[] imageBytes = out.toByteArray();
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                image = FileUtils.rotateImageAndScale(image, 90, maxPictureSize);
                onPhotoTakenSuccessListener.onPhotoTakenSuccess(
                        image,
                        pictureSize.width,
                        pictureSize.height,
                        image.getWidth(),
                        image.getHeight()

                );
            }
        });
    }
}