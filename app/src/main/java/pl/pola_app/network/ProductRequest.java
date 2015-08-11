package pl.pola_app.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import pl.pola_app.model.Product;

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
