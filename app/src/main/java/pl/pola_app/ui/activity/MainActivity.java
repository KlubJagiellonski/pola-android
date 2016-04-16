package pl.pola_app.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.pola_app.BuildConfig;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.SearchResult;
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


public class MainActivity extends AppCompatActivity implements Callback<SearchResult>, ScannerFragment.BarcodeScannedListener {

    @Inject
    Bus eventBus;

    private ProductsListFragment productsListFragment;
    private ScannerFragment scannerFragment;
    private int milisecondsBetweenExisting = 2000;//otherwise it will scan and vibrate few times a second
    private Handler handlerScanner;
    private Runnable runnableResumeScan = new Runnable() {
        @Override
        public void run() {
            if (BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("Scanned")
                        .putCustomAttribute("existing", "true"));
            }
            scannerFragment.resumeScanning();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this, this);
        PolaApplication.component(this).inject(this);
        handlerScanner = new Handler();

        Nammu.init(this);

        productsListFragment = (ProductsListFragment) getFragmentManager().findFragmentById(R.id.product_list_fragment);
        scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment);
        scannerFragment.setOnBarcodeScannedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
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
        if(BuildConfig.USE_CRASHLYTICS) {
            try {
                Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName(event.searchResult.name + "") //As it might be null
                                .putContentType("Open Card")
                                .putContentId(Integer.toString(event.searchResult.product_id))
                                .putCustomAttribute("Code", event.searchResult.code)
                                .putCustomAttribute("DeviceId", Utils.getSessionGuid(this))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in, 0, 0, R.animator.slide_out);
        ProductDetailsFragment newFragment = ProductDetailsFragment.newInstance(event.searchResult);
        ft.add(R.id.container, newFragment, ProductDetailsFragment.class.getName());
        ft.hide(productsListFragment);
        ft.addToBackStack(ProductDetailsFragment.class.getName());
        ft.commitAllowingStateLoss();
    }

    @Subscribe
    public void reportButtonClicked(ReportButtonClickedEvent event) {
        if(event.searchResult.product_id != null) {
            launchReportActivity(Integer.toString(event.searchResult.product_id));
        } else {
            launchReportActivity(null);
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
        if(BuildConfig.USE_CRASHLYTICS) {
            Answers.getInstance().logSearch(new SearchEvent()
                            .putQuery(result)
                            .putCustomAttribute("DeviceId", Utils.getSessionGuid(this))
            );
        }
//        if(productsListFragment.itemExists(result)) {
//           handlerScanner.removeCallbacks(runnableResumeScan);
//            handlerScanner.postDelayed(runnableResumeScan, milisecondsBetweenExisting);
//        } else {
            if(BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("Scanned")
                        .putCustomAttribute("existing", "false"));
            }
            productsListFragment.createProductPlaceholder();

            Api api = PolaApplication.retrofit.create(Api.class);
            Call<SearchResult> reportResultCall = api.getByCode(result, Utils.getSessionGuid(this));
            reportResultCall.enqueue(this);
            if(scannerFragment != null) {
                if (productsListFragment != null && productsListFragment.searchResults != null) {
                    scannerFragment.updateBoxPosition(productsListFragment.searchResults.size());
                }
            }
 //       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResponse(Response<SearchResult> response, Retrofit retrofit) {
        if (BuildConfig.USE_CRASHLYTICS) {
            try {
                Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName(response.body().name + "")//To avoid null as it might be empty
                                .putContentType("Card Preview")
                                .putContentId(Integer.toString(response.body().product_id))
                                .putCustomAttribute("Code", response.code())
                                .putCustomAttribute("DeviceId", Utils.getSessionGuid(this))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(productsListFragment != null) {
            productsListFragment.addProduct(response.body());
        }
        if(scannerFragment != null) {
            scannerFragment.resumeScanning();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if(BuildConfig.USE_CRASHLYTICS) {
            Answers.getInstance().logCustom(new CustomEvent("Barcode request failed")
            .putCustomAttribute("message", t.getLocalizedMessage()));
        }
        if("Unable to resolve host \"www.pola-app.pl\": No address associated with hostname".equals(t.getLocalizedMessage())) {//TODO this is awefull
            Toast.makeText(this, getString(R.string.toast_no_connection), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        handlerScanner.removeCallbacks(runnableResumeScan);
        if(productsListFragment != null) {
            productsListFragment.removeProductPlaceholder();
        }
        if(scannerFragment != null) {
            scannerFragment.resumeScanning();
        }
        if(scannerFragment != null) {
            if (productsListFragment != null && productsListFragment.searchResults != null) {
                scannerFragment.updateBoxPosition(productsListFragment.searchResults.size());
            }
        }
    }
}
