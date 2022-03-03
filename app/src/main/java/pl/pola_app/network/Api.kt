package pl.pola_app.network

import pl.pola_app.model.ReportResult
import okhttp3.RequestBody
import com.google.gson.JsonObject
import pl.pola_app.model.Report
import pl.pola_app.model.SearchResult
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("a/v4/get_by_code")
    fun getByCode(
        @Query("code") barcode: String?,
        @Query("device_id") deviceId: String?,
        @Query("noai") noai: Boolean?
    ): Call<SearchResult?>?

    @GET("a/v4/get_by_code")
    fun getByCode(
        @Query("code") barcode: String?,
        @Query("device_id") deviceId: String?
    ): Call<SearchResult?>?

    @Headers("Content-Encoding: gzip")
    @POST("a/v2/create_report")
    fun createReport(
        @Query("device_id") deviceId: String?,
        @Body report: Report?
    ): Call<ReportResult?>?

    @Headers("Content-Type: image/*")
    @PUT
    fun sendReportImage(@Url url: String?, @Body file: RequestBody?): Call<JsonObject?>?
}