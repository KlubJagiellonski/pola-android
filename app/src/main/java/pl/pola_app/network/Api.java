package pl.pola_app.network;

import com.google.gson.JsonObject;
import com.squareup.okhttp.RequestBody;

import pl.pola_app.model.Product;
import pl.pola_app.model.Report;
import pl.pola_app.model.ReportResult;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Api {

    @GET("/a/v2/get_by_code")
    Call<Product>  product(@Query("code") String barcode, @Query("device_id") String deviceId);

    @Headers("Content-Encoding: gzip")
    @POST("/a/v2/create_report")
    Call<ReportResult> createReport(@Query("device_id") String deviceId, @Body Report report);

    @Multipart
    @POST("/a/v2/attach_file")
    Call<JsonObject> sendReportImage(@Query("device_id") String deviceId, @Query("report_id") String reportId, @Part("file\"; filename=\"image.jpg\"") RequestBody file);

}
