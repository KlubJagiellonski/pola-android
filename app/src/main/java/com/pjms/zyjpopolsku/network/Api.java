package com.pjms.zyjpopolsku.network;

import com.pjms.zyjpopolsku.model.Product;

import retrofit.http.GET;
import retrofit.http.Path;

public interface Api {
    @GET("/product/{barcode}")
    Product product(@Path("barcode") String barcode);
}
