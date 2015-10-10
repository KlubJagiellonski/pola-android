package pl.pola_app.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import javax.inject.Inject;

import pl.pola_app.model.Product;

public class GetProductRequest extends RetrofitSpiceRequest<Product, Api> {
    private String barcode;
    private String deviceId;

    public GetProductRequest(String barcode, String deviceId) {
        super(Product.class, Api.class);
        this.barcode = barcode;
        this.deviceId = deviceId;
    }

    @Override
    public Product loadDataFromNetwork() throws Exception {
        return getService().product(barcode, deviceId);
    }

    public String getCacheKey() {
        return barcode;
    }
}
