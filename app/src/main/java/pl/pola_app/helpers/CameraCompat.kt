package pl.pola_app.helpers

import android.graphics.Bitmap
import android.view.SurfaceHolder

abstract class CameraCompat {
    abstract fun open()
    abstract fun openPreview(surfaceholder: SurfaceHolder)
    abstract fun closePreview()
    abstract fun takePicture(onPhotoTakenSuccessListener: OnPhotoTakenSuccessListener)
    abstract fun release()
    interface OnPhotoTakenSuccessListener {
        fun onPhotoTakenSuccess(
            bitmap: Bitmap,
            originalWidth: Int,
            originalHeight: Int,
            width: Int,
            height: Int
        )
    }

    companion object {
        fun create(minPicSize: Int): CameraCompat {
            return OldCamera(minPicSize)
        }
    }
}