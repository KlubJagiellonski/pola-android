package pl.pola_app.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.ProductsListLinearLayoutManager;
import pl.pola_app.helpers.SessionId;
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
    @Bind(R.id.toolbar)
    Toolbar toolbar;

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
        SessionId sessionId = SessionId.create(this);
        mainPresenter = MainPresenter.create(this, productList, productsAdapter, sessionId, eventBus);


        scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment);
        scannerFragment.setOnBarcodeScannedListener(mainPresenter);

        productsListView.setLayoutManager(new ProductsListLinearLayoutManager(this));

        setupActionBar();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("");
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setHomeButtonEnabled(false);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_about:
                intent = new Intent(this, ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_ABOUT);
                startActivity(intent);
                return true;
            case R.id.action_metodology:
                intent = new Intent(this, ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_METHOD);
                startActivity(intent);
                return true;
            case R.id.action_club:
                intent = new Intent(this, ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_KJ);
                startActivity(intent);
                return true;
            case R.id.action_team:
                intent = new Intent(this, ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_TEAM);
                startActivity(intent);
                return true;
            case R.id.action_partners:
                intent = new Intent(this, ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_PARTNERS);
                startActivity(intent);
                return true;
            case R.id.action_bug:
                intent = new Intent(this, CreateReportActivity.class);
                intent.setAction("product_report");
                startActivity(intent);
                return true;
            case R.id.action_mail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Utils.POLA_MAIL, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pola");
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_picker)));
                return true;
            case R.id.action_rate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                return true;
            case R.id.action_fb:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_FB));
                startActivity(intent);
                return true;
            case R.id.action_twitter:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_TWITTER));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
