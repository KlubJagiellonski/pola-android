package pl.pola_app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import pl.pola_app.PolaApplication;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.SettingsPreference;
import pl.pola_app.model.SearchResult;
import pl.pola_app.network.Api;
import pl.pola_app.ui.adapter.OnProductListChanged;
import pl.pola_app.ui.adapter.ProductList;
import pl.pola_app.ui.adapter.ProductsAdapter;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;
import pl.pola_app.ui.fragment.BarcodeListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MainPresenter implements Callback<SearchResult>, BarcodeListener {
    private static final int millisecondsBetweenExisting = 2000;//otherwise it will scan and vibrate few times a second
    private static final int NUMBER_OF_TIMES_WHEN_SHOW_RATE_DIALOG = 3; //When user scan 3 barcodes show rate dialog

    private boolean isRateDialogPrompted = false; //Don't try run dialog twice in one session
    private final MainViewBinder viewBinder;
    private final ProductList productList;
    private final Handler handlerScanner = new Handler();
    private final EventLogger logger;
    @Nullable
    private Call<SearchResult> reportResultCall;
    private final Runnable runnableResumeScan = new Runnable() {
        @Override
        public void run() {
            viewBinder.resumeScanning();
        }
    };
    private Api api;
    private SessionId sessionId;
    private Bus eventBus;
    private SettingsPreference settingsPreference;
    SearchResult currentSearchResult;

    public static MainPresenter create(Context applicationContext, @NonNull final MainViewBinder viewBinder,
                                       @NonNull final ProductList productList,
                                       @NonNull final ProductsAdapter productsAdapter,
                                       @NonNull SessionId sessionId,
                                       @NonNull final Bus eventBus,
                                       @NonNull final SettingsPreference settingsPreference) {


        productList.setOnProductListChanged(new OnProductListChanged() {
            @Override
            public void onChanged() {
                productsAdapter.notifyDataSetChanged();
            }
        });

        viewBinder.setAdapter(productsAdapter);
        final Api api = PolaApplication.retrofit.create(Api.class);

        final EventLogger logger = new EventLogger(applicationContext);
        final MainPresenter mainPresenter = new MainPresenter(viewBinder, productList, api, logger, sessionId, eventBus, settingsPreference);
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
                  @NonNull Bus eventBus,
                  @NonNull SettingsPreference settingsPreference) {
        this.viewBinder = viewBinder;
        this.productList = productList;
        this.api = api;
        this.logger = logger;
        this.sessionId = sessionId;
        this.eventBus = eventBus;
        this.settingsPreference = settingsPreference;
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
    public void onBarcode(String barcode, boolean fromCamera) {
        logger.logSearch(barcode, sessionId.get(), fromCamera ? "camera" : "keyboard");
        if (productList.itemExists(barcode)) {
            handlerScanner.removeCallbacks(runnableResumeScan);
            handlerScanner.postDelayed(runnableResumeScan, millisecondsBetweenExisting);
        } else {
            productList.createProductPlaceholder();

            reportResultCall =
                    viewBinder.getDeviceYear() < 2010
                            ? api.getByCode(barcode, sessionId.get(), true)
                            : api.getByCode(barcode, sessionId.get());
            reportResultCall.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
        final SearchResult searchResult = response.body();
        if (searchResult == null) {
            return;
        }
        currentSearchResult = searchResult;
        logger.logContentView((searchResult.name != null) ? searchResult.name + "" : "empty",
                "company_received",
                String.valueOf(searchResult.product_id),
                (searchResult.code != null) ? searchResult.code : "empty",
                sessionId.get());
        productList.addProduct(searchResult);
        viewBinder.resumeScanning();
        viewBinder.setSupportPolaAppButtonVisibility(searchResult.askForSupport(), searchResult);
        if (isShouldShowRatingDialog()) {
            showNewRatePrompt();
        }
        settingsPreference.increaseSuccessCounter();
    }

    private void showNewRatePrompt() {
        if (isRateDialogPrompted == false) {
            isRateDialogPrompted = true;
            viewBinder.showNewRatePrompt();
        }
    }

    private boolean isShouldShowRatingDialog() {
        return settingsPreference.getSuccessScanCounter() % NUMBER_OF_TIMES_WHEN_SHOW_RATE_DIALOG == 0;
    }

    @Override
    public void onFailure(Call<SearchResult> call, Throwable t) {
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
                "card_opened",
                String.valueOf(searchResult.product_id),
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

    public void setCurrentSearchResult(SearchResult currentSearchResult) {
        this.currentSearchResult = currentSearchResult;
    }

    public void onBackStackChange(boolean isNotBackStackEmpty) {
        viewBinder.setSupportPolaAppButtonVisibility(!isNotBackStackEmpty && currentSearchResult != null && currentSearchResult.askForSupport(), currentSearchResult);
    }

    public void onSupportPolaButtonClick() {
        if (currentSearchResult != null) {
            viewBinder.openWww(currentSearchResult, currentSearchResult.donate.url);
        }
    }

    public void onSupportPolaFinished() {
        if (currentSearchResult != null) {
            currentSearchResult.donate = null;
            viewBinder.setSupportPolaAppButtonVisibility(false, currentSearchResult);

        }
    }
}
