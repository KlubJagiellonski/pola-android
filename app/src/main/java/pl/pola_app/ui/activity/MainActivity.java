package pl.pola_app.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Product;
import pl.pola_app.network.GetProductRequest;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;
import pl.pola_app.ui.event.ProductItemClickedEvent;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
import pl.pola_app.ui.fragment.ProductsListFragment;
import pl.pola_app.ui.fragment.ScannerFragment;


public class MainActivity extends AppCompatActivity implements ScannerFragment.BarcodeScannedListener, RequestListener<Product> {
    @Inject
    SpiceManager spiceManager;

    @Inject
    Bus eventBus;

    private ProductsListFragment productsListFragment;
    private ScannerFragment scannerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this, this);
        PolaApplication.component(this).inject(this);
        eventBus.register(this);

        productsListFragment = (ProductsListFragment) getFragmentManager().findFragmentById(R.id.product_list_fragment);
        scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment);
        scannerFragment.setOnBarcodeScannedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        spiceManager.addListenerIfPending(Product.class, null, this);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void productItemClicked(ProductItemClickedEvent event) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in, 0, 0, R.animator.slide_out);
        ProductDetailsFragment newFragment = ProductDetailsFragment.newInstance(event.product);
        ft.add(R.id.container, newFragment, ProductDetailsFragment.class.getName());
        ft.hide(productsListFragment);
        ft.addToBackStack(ProductDetailsFragment.class.getName());
        ft.commit();
    }

    @Subscribe
    public void productDetailsFragmentDismissed(ProductDetailsFragmentDismissedEvent event) {
        getFragmentManager().popBackStack(ProductDetailsFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void barcodeScanned(String result) {
        if(productsListFragment.itemExists(result)) {
            scannerFragment.resumeScanning();
        } else {
            productsListFragment.createProductPlaceholder();
            GetProductRequest productRequest = new GetProductRequest(result, Utils.getDeviceId(this));
            spiceManager.execute(productRequest, productRequest.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, MainActivity.this);
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, spiceException.toString(), Toast.LENGTH_SHORT).show();
        productsListFragment.removeProductPlaceholder();
        scannerFragment.resumeScanning();
    }

    @Override
    public void onRequestSuccess(Product product) {
        productsListFragment.addProduct(product);
        scannerFragment.resumeScanning();
    }
}
