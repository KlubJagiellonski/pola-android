package pl.pola_app.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Tajchert on 28.10.2015.
 */
data class Report(
    @field:SerializedName("description") val description: String,
    @field:SerializedName("files_count") val filesCount: Int,
    @field:SerializedName("mime_type") val mimeType: String,
    @field:SerializedName("file_ext") val fileExt: String
) {
    @SerializedName("product_id")
    var productId: String = ""

    constructor(
        description: String,
        productId: String,
        filesCount: Int,
        mimeType: String,
        fileExt: String
    ) : this(description, filesCount, mimeType, fileExt) {
        this.productId = productId
    }
}