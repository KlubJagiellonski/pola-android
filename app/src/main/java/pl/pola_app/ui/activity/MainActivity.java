package pl.pola_app.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.pola_app.BuildConfig;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Product;
import pl.pola_app.network.Api;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;
import pl.pola_app.ui.event.ProductItemClickedEvent;
import pl.pola_app.ui.event.ReportButtonClickedEvent;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
import pl.pola_app.ui.fragment.ProductsListFragment;
import pl.pola_app.ui.fragment.ScannerFragment;
import pl.tajchert.nammu.Nammu;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends AppCompatActivity implements Callback<Product>, ScannerFragment.BarcodeScannedListener {

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

        Nammu.init(this);

        productsListFragment = (ProductsListFragment) getFragmentManager().findFragmentById(R.id.product_list_fragment);
        scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment);
        scannerFragment.setOnBarcodeScannedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        if(event.product.company == null && event.product.report.equals(getResources().getString(R.string.ask_for_company_property_name))) {
            if(event.product != null) {
                launchReportActivity(event.product.code);
            } else {
                launchReportActivity(null);
            }
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.slide_in, 0, 0, R.animator.slide_out);
            ProductDetailsFragment newFragment = ProductDetailsFragment.newInstance(event.product);
            ft.add(R.id.container, newFragment, ProductDetailsFragment.class.getName());
            ft.hide(productsListFragment);
            ft.addToBackStack(ProductDetailsFragment.class.getName());
            ft.commit();
        }
    }

    @Subscribe
    public void reportButtonClicked(ReportButtonClickedEvent event) {
        if(event.product != null) {
            launchReportActivity(event.product.code);
        }
    }

    private void launchReportActivity(String productId) {
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.setAction("product_report");
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    @Subscribe
    public void productDetailsFragmentDismissed(ProductDetailsFragmentDismissedEvent event) {
        getFragmentManager().popBackStack(ProductDetailsFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void barcodeScanned(String result) {
        if(productsListFragment.itemExists(result)) {
            if(BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("Scanned")
                        .putCustomAttribute("existing", "true"));
            }
            scannerFragment.resumeScanning();
        } else {
            if(BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("Scanned")
                        .putCustomAttribute("existing", "false"));
            }
            productsListFragment.createProductPlaceholder();

            Api api = PolaApplication.retrofit.create(Api.class);
            Call<Product> reportResultCall = api.product(result, Utils.getDeviceId(this));
            reportResultCall.enqueue(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResponse(Response<Product> response, Retrofit retrofit) {
        productsListFragment.addProduct(response.body());
        scannerFragment.resumeScanning();
    }

    @Override
    public void onFailure(Throwable t) {
        if(BuildConfig.USE_CRASHLYTICS) {
            Answers.getInstance().logCustom(new CustomEvent("Barcode request failed"));
        }
        Toast.makeText(this, t.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
        productsListFragment.removeProductPlaceholder();
        scannerFragment.resumeScanning();
    }
}
