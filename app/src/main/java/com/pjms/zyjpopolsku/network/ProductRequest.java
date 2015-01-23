package com.pjms.zyjpopolsku.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.pjms.zyjpopolsku.model.Product;

public class ProductRequest extends RetrofitSpiceRequest<Product, Api> {
    private String barcode;

    public ProductRequest(String barcode) {
        super(Product.class, Api.class);
        this.barcode = barcode;
    }

    @Override
    public Product loadDataFromNetwork() throws Exception {
        return getService().product(barcode);
    }
}
