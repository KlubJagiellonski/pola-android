package pl.pola_app.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import pl.pola_app.model.Product;

public class ProductRequest extends RetrofitSpiceRequest<Product, Api> {
    private String barcode;
    private String deviceId;

    public ProductRequest(String barcode, String deviceId) {
        super(Product.class, Api.class);
        this.barcode = barcode;
        this.deviceId = deviceId;
    }

    @Override
    public Product loadDataFromNetwork() throws Exception {
        return getService().product(barcode, deviceId);
    }
}
