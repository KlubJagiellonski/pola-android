package pl.pola_app.model

import com.google.gson.annotations.SerializedName

data class PhotoPicturesReport private constructor(
    @SerializedName("product_id")
    val productId: Int = 0,

    @SerializedName("files_count")
    val filesCount: Int = 0,

    @SerializedName("file_ext")
    val fileExt: String,

    @SerializedName("mime_type")
    val mineType: String,

    @SerializedName("original_width")
    val originalWidth: Int = 0,

    @SerializedName("original_height")
    val originalHeight: Int = 0,

    @SerializedName("width")
    val width: Int = 0,

    @SerializedName("height")
    val height: Int = 0,

    @SerializedName("device_name")
    val deviceName: String,

    @SerializedName("flash_used")
    val flashUsed: Boolean = false,

    @SerializedName("was_portrait")
    val wasPortrait: Boolean = false
){


    class Builder {
        var productId = 0
            private set
        var filesCount = 0
            private set
        var originalWidth = 0
            private set
        var originalHeight = 0
            private set
        var width = 0
            private set
        var height = 0
            private set
        var deviceName: String = ""
            private set

        fun productId(productId: Int) = apply { this.productId = productId }
        fun filesCount(filesCount: Int) = apply { this.filesCount = filesCount }
        fun originalWidth(originalWidth: Int) = apply { this.originalWidth = originalWidth }
        fun originalHeight(originalHeight: Int) = apply { this.originalHeight = originalHeight }
        fun width(width: Int) = apply { this.width = width }
        fun height(height: Int) = apply { this.height = height }
        fun deviceName(deviceName: String) = apply { this.deviceName = deviceName }

        fun build(): PhotoPicturesReport {
            return PhotoPicturesReport(
                productId,
                filesCount,
                fileExt = "jpg",
                mineType = "image/*",
                originalWidth,
                originalHeight,
                width,
                height,
                deviceName,
                flashUsed = false,
                wasPortrait = true
            )
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}