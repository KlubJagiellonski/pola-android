package pl.pola_app.helpers

import android.annotation.TargetApi
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.view.SurfaceHolder
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException

@TargetApi(21)
class OldCamera internal constructor(private val maxPictureSize: Int) : CameraCompat() {
    private lateinit var camera: Camera
    private var safeToTakePicture = false
    private var isPreviewing = false
    override fun open() {
        camera = Camera.open()
        camera.cancelAutoFocus()
        val parameters = camera.parameters
        parameters.previewFormat = ImageFormat.NV21
        parameters.setRecordingHint(true)
        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        try {
            camera.parameters = parameters
        } catch (e: Exception) {
            Timber.e("Error setting camera parameters")
        }
        camera.setDisplayOrientation(90)
    }

    override fun openPreview(surface: SurfaceHolder) {
        try {
            camera.setPreviewDisplay(surface)
            camera.startPreview()
            isPreviewing = true
            safeToTakePicture = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun closePreview() {
        isPreviewing = false
        safeToTakePicture = false
        camera.stopPreview()
    }

    override fun release() {
        camera.release()
    }

    override fun takePicture(onPhotoTakenSuccessListener: OnPhotoTakenSuccessListener) {
        if (!safeToTakePicture) {
            Timber.e("Skipped photo")
            return
        }
        camera.setOneShotPreviewCallback { data: ByteArray, camera1: Camera ->
            if (data.isNotEmpty()) {
                val parameters = camera1.parameters
                val pictureSize = parameters.previewSize
                val imageFormat = parameters.previewFormat
                val out = ByteArrayOutputStream()
                val yuvImage =
                    YuvImage(data, imageFormat, pictureSize.width, pictureSize.height, null)
                yuvImage.compressToJpeg(Rect(0, 0, pictureSize.width, pictureSize.height), 100, out)
                val imageBytes = out.toByteArray()
                var image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                image = image.rotateImageAndScale(90f, maxPictureSize)
                onPhotoTakenSuccessListener.onPhotoTakenSuccess(
                    image,
                    pictureSize.width,
                    pictureSize.height,
                    image.width,
                    image.height
                )
            }
        }
    }
}