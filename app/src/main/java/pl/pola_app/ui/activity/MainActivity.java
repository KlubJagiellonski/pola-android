package pl.pola_app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Bus;

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

    @Inject
    SpiceManager spiceManager;

    @Inject
    Bus eventBus;

    @Bind(R.id.scanner_view)
    ZXingScannerView zXingView;

    @Bind(R.id.products_list)
    RecyclerView productsList;

    private ProductsAdapter productsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PolaApplication.component(this).inject(this);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {

        }
        
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

        productsAdapter = new ProductsAdapter();
        productsList.setAdapter(productsAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
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
        spiceManager.shouldStop();
        super.onStop();
        eventBus.unregister(this);
    }


    @Override
    public void handleResult(Result result) {
        Timber.d(result.getText());
        Timber.d(result.getBarcodeFormat().toString());

        GetProductRequest productRequest = new GetProductRequest(result.getText(), Utils.getDeviceId(this));
        spiceManager.execute(productRequest, result.getText(), DurationInMillis.ONE_HOUR, this);
        zXingView.startCamera();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(Product product) {
        Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();
        productsAdapter.addProduct(product);
    }
}
