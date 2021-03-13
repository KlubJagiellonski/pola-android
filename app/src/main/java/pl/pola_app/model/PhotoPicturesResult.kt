package pl.pola_app.model

import com.google.gson.annotations.SerializedName

data class PhotoPicturesResult (
    @SerializedName("width")
    val width: Int = 0,

    @SerializedName("height")
    val height: Int = 0,

    @SerializedName("signed_requests")
    val signedRequests: List<String>,

    ){
    fun signedRequestsSize(): Int = signedRequests.size
    fun getSignedRequestAt(index: Int) = signedRequests[index]
    override fun toString(): String {
        return "PhotoPicturesResult{" +
                "width=" + width +
                ", height=" + height +
                ", signedRequests=" + signedRequests +
                '}'
    }
}