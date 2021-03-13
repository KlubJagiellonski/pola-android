package pl.pola_app.helpers

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.FileOutputStream
import java.io.IOException


const val QUALITY = 90
fun Bitmap.saveBitmap(picturePath: String) {
    lateinit var out: FileOutputStream
    try {
        out = FileOutputStream(picturePath)
        this.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun Bitmap.rotateImageAndScale(angle: Float, maxSize: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    val width = this.width
    val height = this.height
    val sourceBitmap =
        if (maxSize >= width && maxSize >= height) this else if (width > height) Bitmap.createScaledBitmap(
            this,
            maxSize,
            height * maxSize / width,
            true
        ) else Bitmap.createScaledBitmap(this, width * maxSize / height, maxSize, true)
    return Bitmap.createBitmap(
        sourceBitmap,
        0,
        0,
        sourceBitmap.width,
        sourceBitmap.height,
        matrix,
        true
    )
}
