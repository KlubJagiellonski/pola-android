package pl.pola_app.ui.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import pl.pola_app.model.SearchResult;

interface MainViewBinder {
    void setAdapter(@NonNull RecyclerView.Adapter adapter);

    void resumeScanning();

    void turnOffTorch();

    void openProductDetails(@NonNull SearchResult searchResult);

    void showNoConnectionMessage();

    void showErrorMessage(String message);

    void dismissProductDetailsView();

    void launchReportActivity(String productId, String code);

    void displayHelpMessageDialog(SearchResult searchResult);

    void displayVideoActivity(SearchResult searchResult, String deviceId);

    void openWww(SearchResult searchResult, String url);

    void setSupportPolaAppButtonVisibility(boolean isVisible, SearchResult searchResult);

    int getDeviceYear();
}
