package pl.pola_app.repository

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PolaApi {

    @GET("a/v3/get_by_code")
    fun getByCode(
        @Query("code") barcode: String,
        @Query("device_id") deviceId: String,
        @Query("noai") noai: Boolean? = null
    ): Single<SearchResult>

}

data class SearchResult(
    var product_id: Int? = 0,
    var code: String? = "",
    var name: String? = "",
    var card_type: String? = "",
    var plScore: Int? = 0,
    var altText: String? = "",
    var plCapital: Int? = 0,
    var plWorkers: Int? = 0,
    var plRnD: Int? = 0,
    var plRegistered: Int? = 0,
    var plNotGlobEnt: Int? = 0,
    var is_friend: Boolean? = false,
    var description: String? = "",
    var report_text: String? = "",
    var report_button_text: String? = "",
    var report_button_type: String? = "",
    var friend_text: String? = "",
    var donate: Donate?
)

data class Donate(
    var show_button: Boolean = false,
    var url: String = "",
    var title: String = ""
)