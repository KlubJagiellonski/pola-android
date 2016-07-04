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

    String getSessionId(); //TODO: remove it

    void dismissProductDetailsView();

    void launchReportActivity(String productId);
}
