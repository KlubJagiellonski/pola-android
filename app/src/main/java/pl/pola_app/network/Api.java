package pl.pola_app.network;

import com.google.gson.JsonObject;
import com.squareup.okhttp.RequestBody;

import pl.pola_app.model.Product;
import pl.pola_app.model.Report;
import pl.pola_app.model.ReportResult;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Api {

    @GET("/a/get_by_code/{barcode}")
    Call<Product>  product(@Path("barcode") String barcode, @Query("device_id") String deviceId);

    @POST("/a/create_report")
    Call<ReportResult> createReport(@Query("device_id") String deviceId, @Query("product_id") String productId, @Body Report report);

    @Multipart
    @POST("/a/attach_file")
    Call<JsonObject> sendReportImage(@Query("device_id") String deviceId, @Query("report_id") String reportId, @Part("file\"; filename=\"test.jpg\"") RequestBody file);

}
