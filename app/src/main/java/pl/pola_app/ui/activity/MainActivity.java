package pl.pola_app.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.device.yearclass.YearClass;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.ProductsListLinearLayoutManager;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.SettingsPreference;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.adapter.ProductList;
import pl.pola_app.ui.adapter.ProductsAdapter;
import pl.pola_app.ui.delegate.ProductDetailsFragmentDelegate;
import pl.pola_app.ui.event.FlashActionListener;
import pl.pola_app.ui.fragment.BarcodeListener;
import pl.pola_app.ui.fragment.KeyboardFragment;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
import pl.pola_app.ui.fragment.ScannerFragment;
import pl.tajchert.nammu.Nammu;


public class MainActivity extends AppCompatActivity implements MainViewBinder, BarcodeListener, ProductDetailsFragmentDelegate {

    private static final int DONATE_POLA = 1000;
    @Inject
    Bus eventBus;
    @Inject
    SettingsPreference settingsPreference;
    @BindView(R.id.products_list)
    RecyclerView productsListView;
    @BindView(R.id.open_keyboard_button)
    FloatingActionButton openKeyboard;
    @BindView(R.id.support_pola_app)
    Button supportPolaApp;
    @BindView(R.id.menu)
    ImageView menu;

    private ScannerFragment scannerFragment;
    private MainPresenter mainPresenter;
    private EventLogger logger;
    private SessionId sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this, this);
        PolaApplication.component(this).inject(this);
        Nammu.init(this);

        ProductList productList = ProductList.create(savedInstanceState);
        final ProductsAdapter productsAdapter = new ProductsAdapter(this, productList);
        sessionId = SessionId.create(this);
        mainPresenter = MainPresenter.create(getApplicationContext(), this, productList, productsAdapter, sessionId, eventBus);

        logger = new EventLogger(this);

        openKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKeyboard();
            }
        });
        scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment);

        productsListView.setLayoutManager(new ProductsListLinearLayoutManager(this));

        getFragmentManager().addOnBackStackChangedListener(() -> {
            final boolean isNotBackStackEmpty = getFragmentManager().getBackStackEntryCount() > 0;
            mainPresenter.onBackStackChange(isNotBackStackEmpty);
            if (isNotBackStackEmpty) {
                openKeyboard.hide();
            } else {
                openKeyboard.show();
            }
        });

        menu.setOnClickListener(view -> {
            startActivity(new Intent(this, MenuActivity.class));
        });
    }

    @OnClick(R.id.flash_icon)
    public void onFlashIconClicked(View view) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.scanner_fragment);
        if (fragment != null && fragment instanceof FlashActionListener) {
            final FlashActionListener flashActionListener = (FlashActionListener) fragment;
            flashActionListener.onFlashAction();
            if (view != null && view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(this,
                        flashActionListener.isTorchOn() ? R.drawable.ic_flash_off_white_48dp : R.drawable.ic_flash_on_white_48dp));
            }
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
        if (searchResult.askForSupport()) {
            supportPolaApp.setVisibility(View.VISIBLE);
            supportPolaApp.setText(searchResult.donate.title);
        } else {
            supportPolaApp.setVisibility(View.GONE);
        }
        mainPresenter.setCurrentSearchResult(searchResult);
    }

    public void openKeyboard() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            return; // prevent adding fragment twice
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, 0, 0, R.animator.fade_out);
        KeyboardFragment newFragment = new KeyboardFragment();
        ft.add(R.id.container, newFragment, KeyboardFragment.class.getName());
        ft.addToBackStack(KeyboardFragment.class.getName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void setAdapter(@NonNull final RecyclerView.Adapter adapter) {
        productsListView.setAdapter(adapter);
    }

    @OnClick(R.id.support_pola_app)
    public void onSupportPolaButtonClick() {
        mainPresenter.onSupportPolaButtonClick();
    }

    @Override
    public void onsSeePolaFriendsAction() {
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("url", pl.pola_app.helpers.Utils.URL_POLA_FRIENDS);
        startActivity(intent);
    }

    @Override
    public void setSupportPolaAppButtonVisibility(boolean isVisible, SearchResult searchResult) {
        if (isVisible) {
            supportPolaApp.setVisibility(View.VISIBLE);
            supportPolaApp.setText(searchResult.donate.title);
            return;
        }
        supportPolaApp.setVisibility(View.GONE);
    }

    @Override
    public void openWww(SearchResult searchResult, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void onBarcode(String barcode, boolean fromCamera) {
        mainPresenter.onBarcode(barcode, fromCamera);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == DONATE_POLA) {
            mainPresenter.onSupportPolaFinished();
        }
    }

    @Override
    public int getDeviceYear() {
        return YearClass.get(getApplication());
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
    public void launchReportActivity(String productId, String code) {
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.setAction("product_report");
        intent.putExtra("productId", productId);
        intent.putExtra("code", code);
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
        switch (item.getItemId()) {
            case R.id.action_menu:
                startActivity(new Intent(this, MenuActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
