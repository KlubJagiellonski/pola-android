package pl.pola_app.network;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import pl.pola_app.model.PhotoPicturesReport;
import pl.pola_app.model.PhotoPicturesResult;
import pl.pola_app.model.SearchResult;
import pl.pola_app.model.Report;
import pl.pola_app.model.ReportResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Api {
    @GET("a/v3/get_by_code")
    Call<SearchResult> getByCode(@Query("code") String barcode, @Query("device_id") String deviceId, @Query("noai") Boolean noai);

    @GET("a/v3/get_by_code")
    Call<SearchResult> getByCode(@Query("code") String barcode, @Query("device_id") String deviceId);

    @Headers("Content-Encoding: gzip")
    @POST("a/v2/create_report")
    Call<ReportResult> createReport(@Query("device_id") String deviceId, @Body Report report);

    @Headers({"x-amz-acl: public-read", "Content-Type: image/*"})
    @PUT
    Call<JsonObject> sendReportImage(@Url String url, @Body RequestBody file);

    @POST("a/v3/add_ai_pics")
    Observable<PhotoPicturesResult> addAiPics(@Query("device_id") String deviceId, @Body PhotoPicturesReport photoPicturesReport);


    @Headers({"x-amz-acl: public-read", "Content-Type: image/*"})
    @PUT
    Observable<Void> sendReportImageAsObservable(@Url String url, @Body RequestBody file);
}
