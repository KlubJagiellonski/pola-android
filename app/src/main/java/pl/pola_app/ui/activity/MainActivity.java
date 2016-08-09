package pl.pola_app.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.ProductsListLinearLayoutManager;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.adapter.ProductList;
import pl.pola_app.ui.adapter.ProductsAdapter;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
import pl.pola_app.ui.fragment.ScannerFragment;
import pl.tajchert.nammu.Nammu;


public class MainActivity extends AppCompatActivity implements MainViewBinder {

    @Inject
    Bus eventBus;
    @Bind(R.id.products_list)
    RecyclerView productsListView;
    @Inject
    ProductsListLinearLayoutManager productsListLinearLayoutManager;

    private ScannerFragment scannerFragment;
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this, this);
        PolaApplication.component(this).inject(this);
        Nammu.init(this);

        ProductList productList = ProductList.create(savedInstanceState);
        productList.setLogger(new EventLogger());
        final ProductsAdapter productsAdapter = new ProductsAdapter(this, productList);
        mainPresenter = MainPresenter.create(this, productList, productsAdapter, eventBus);


        scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment);
        scannerFragment.setOnBarcodeScannedListener(mainPresenter);

        productsListView.setLayoutManager(productsListLinearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainPresenter.register();
    }

    @Override
    protected void onStop() {
        mainPresenter.unregister();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainPresenter.onSaveState(outState);
    }

    @Override
    public void openProductDetails(@NonNull final SearchResult searchResult) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in, 0, 0, R.animator.slide_out);
        ProductDetailsFragment newFragment = ProductDetailsFragment.newInstance(searchResult);
        ft.add(R.id.container, newFragment, ProductDetailsFragment.class.getName());
        ft.addToBackStack(ProductDetailsFragment.class.getName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void setAdapter(@NonNull final RecyclerView.Adapter adapter) {
        productsListView.setAdapter(adapter);
    }

    @Override
    public void resumeScanning() {
        scannerFragment.resumeScanning();
    }

    @Override
    public void turnOffTorch() {
        scannerFragment.setTorchOff();
    }

    @Override
    public void showNoConnectionMessage() {
        Toast.makeText(this, getString(R.string.toast_no_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getSessionId() {
        return Utils.getSessionGuid(this);
    }

    @Override
    public void launchReportActivity(String productId) {
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.setAction("product_report");
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    @Override
    public void dismissProductDetailsView() {
        getFragmentManager().popBackStack(ProductDetailsFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
