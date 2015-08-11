package pl.pola_app.network;

import pl.pola_app.model.Product;

import retrofit.http.GET;
import retrofit.http.Path;

public interface Api {
    @GET("/product/{barcode}")
    Product product(@Path("barcode") String barcode);
}
