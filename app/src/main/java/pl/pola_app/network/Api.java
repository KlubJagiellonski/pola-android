package pl.pola_app.network;

import pl.pola_app.model.Product;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Api {

    @GET("/cms/product/get_by_code/{barcode}")
    Product product(@Path("barcode") String barcode, @Query("device_id") String deviceId);

}
