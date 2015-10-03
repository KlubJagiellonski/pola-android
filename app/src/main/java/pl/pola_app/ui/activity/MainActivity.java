package pl.pola_app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Product;
import pl.pola_app.network.GetProductRequest;
import pl.pola_app.ui.adapter.ProductsAdapter;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler, RequestListener<Product> {

    private static final String REQUEST_CACHE_KEY = "request_cache_key";
    @Inject
    SpiceManager spiceManager;

    @Bind(R.id.scanner_view)
    ZXingScannerView zXingView;

    @Bind(R.id.products_list)
    RecyclerView productsList;

    @Inject
    ProductsAdapter productsAdapter;

    private RecyclerView.LayoutManager layoutManager;
    private GetProductRequest productRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PolaApplication.component(this).inject(this);
        ButterKnife.bind(this);

        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

        productsList.setAdapter(productsAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingView.setResultHandler(this);
        zXingView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        productsAdapter.onSaveInstanceState(outState);

        if(productRequest != null) {
            outState.putString(REQUEST_CACHE_KEY, productRequest.getCacheKey());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        productsAdapter.onRestoreInstanceSate(savedInstanceState);

        String spiceRequestCacheKey = savedInstanceState.getString(REQUEST_CACHE_KEY);
        if(spiceRequestCacheKey != null) {
            spiceManager.addListenerIfPending(Product.class, spiceRequestCacheKey, this);
        }
    }

    @Override
    public void handleResult(Result result) {
        Timber.d(result.getText());
        Timber.d(result.getBarcodeFormat().toString());

        productRequest = new GetProductRequest(result.getText(), Utils.getDeviceId(this));
        spiceManager.execute(productRequest, productRequest.getCacheKey(), DurationInMillis.ONE_HOUR, this);
        zXingView.startCamera();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, spiceException.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Product product) {
        if(product != null) {
            productsAdapter.addProduct(product);
        }
    }
}
