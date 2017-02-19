package pl.pola_app.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import pl.pola_app.PolaApplication;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.model.SearchResult;
import pl.pola_app.network.Api;
import pl.pola_app.ui.adapter.OnProductListChanged;
import pl.pola_app.ui.adapter.ProductList;
import pl.pola_app.ui.adapter.ProductsAdapter;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;
import pl.pola_app.ui.event.ReportButtonClickedEvent;
import pl.pola_app.ui.fragment.ScannerFragment;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

class MainPresenter implements Callback<SearchResult>, ScannerFragment.BarcodeScannedListener {
    private static final int millisecondsBetweenExisting = 2000;//otherwise it will scan and vibrate few times a second
    private final MainViewBinder viewBinder;
    private final ProductList productList;
    private final Handler handlerScanner = new Handler();
    private final EventLogger logger;
    @Nullable private Call<SearchResult> reportResultCall;
    private final Runnable runnableResumeScan = new Runnable() {
        @Override
        public void run() {
            logger.logCustom("Scanned", new Pair<>("existing", "true"));
            viewBinder.resumeScanning();
        }
    };
    private Api api;
    private SessionId sessionId;
    private Bus eventBus;

    public static MainPresenter create(@NonNull final MainViewBinder viewBinder,
                                       @NonNull final ProductList productList,
                                       @NonNull final ProductsAdapter productsAdapter,
                                       @NonNull SessionId sessionId,
                                       @NonNull final Bus eventBus) {

        productList.setOnProductListChanged(new OnProductListChanged() {
            @Override
            public void onChanged() {
                productsAdapter.notifyDataSetChanged();
            }
        });

        viewBinder.setAdapter(productsAdapter);
        final Api api = PolaApplication.retrofit.create(Api.class);

        final EventLogger logger = new EventLogger();
        final MainPresenter mainPresenter = new MainPresenter(viewBinder, productList, api, logger, sessionId, eventBus);
        productsAdapter.setOnProductClickListener(new ProductsAdapter.ProductClickListener() {
            @Override
            public void itemClicked(SearchResult searchResult) {
                mainPresenter.onItemClicked(searchResult);
            }
        });
        return mainPresenter;
    }

    MainPresenter(@NonNull MainViewBinder viewBinder,
                  @NonNull ProductList productList,
                  @NonNull Api api,
                  @NonNull EventLogger logger,
                  @NonNull SessionId sessionId,
                  @NonNull Bus eventBus) {
        this.viewBinder = viewBinder;
        this.productList = productList;
        this.api = api;
        this.logger = logger;
        this.sessionId = sessionId;
        this.eventBus = eventBus;
    }

    void register() {
        eventBus.register(this);
    }

    void unregister() {
        eventBus.unregister(this);
        if (reportResultCall != null) {
            reportResultCall.cancel();
        }
    }

    @Override
    public void barcodeScanned(String result) {
        logger.logSearch(result, sessionId.get());
        if (productList.itemExists(result)) {
            handlerScanner.removeCallbacks(runnableResumeScan);
            handlerScanner.postDelayed(runnableResumeScan, millisecondsBetweenExisting);
        } else {
            logger.logCustom("Scanned", new Pair<>("existing", "false"));
            productList.createProductPlaceholder();

            reportResultCall = api.getByCode(result, sessionId.get());
            reportResultCall.enqueue(this);
        }
    }

    @Override
    public void onResponse(Response<SearchResult> response, Retrofit retrofit) {
        logger.logContentView(response.body().name + "",
                "Card Preview"
                , Integer.toString(response.body().product_id),
                String.valueOf(response.code()),
                sessionId.get());
        productList.addProduct(response.body());
        viewBinder.resumeScanning();
    }

    @Override
    public void onFailure(Throwable t) {
        logger.logCustom("Barcode request failed", new Pair<>("message", t.getLocalizedMessage()));
        if ("Unable to resolve host \"www.pola-app.pl\": No address associated with hostname".equals(t.getLocalizedMessage())) {//TODO this is awefull
            viewBinder.showNoConnectionMessage();
        } else {
            viewBinder.showErrorMessage(t.getLocalizedMessage());
        }
        handlerScanner.removeCallbacks(runnableResumeScan);
        productList.removeProductPlaceholder();
        viewBinder.resumeScanning();
    }

    void onItemClicked(@NonNull final SearchResult searchResult) {
        logger.logContentView(searchResult.name + "",
                "Open Card",
                Integer.toString(searchResult.product_id),
                searchResult.code,
                sessionId.get());
        viewBinder.turnOffTorch();
        viewBinder.openProductDetails(searchResult);
    }

    void onSaveState(@NonNull final Bundle bundle) {
        productList.writeToBundle(bundle);
    }

    @Subscribe
    @SuppressWarnings("WeakerAccess")
    public void productDetailsFragmentDismissed(@SuppressWarnings("UnusedParameters") ProductDetailsFragmentDismissedEvent event) {
        viewBinder.dismissProductDetailsView();
    }

    @Subscribe
    @SuppressWarnings("WeakerAccess")
    public void reportButtonClicked(ReportButtonClickedEvent event) {
        String productId = null;
        if (event.searchResult.product_id != null) {
            productId = Integer.toString(event.searchResult.product_id);
        }
        viewBinder.launchReportActivity(productId);
    }
}
