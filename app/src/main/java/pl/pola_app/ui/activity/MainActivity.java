package pl.pola_app.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.model.Product;
import pl.pola_app.ui.events.ProductRequestSuccessEvent;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
import pl.pola_app.ui.fragment.ScannerFragment;


public class MainActivity extends ActionBarActivity {

    @Inject
    Bus eventBus;

    @Bind(R.id.container)
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PolaApplication.component(this).inject(this);
        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            return;
        }

        showScannerFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    @Subscribe
    public void productRequestSuccess(ProductRequestSuccessEvent event) {
        showProductDetailsFragment(event.getProduct());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void showScannerFragment() {
        ScannerFragment scannerFragment = ScannerFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.container, scannerFragment).commit();
    }

    private void showProductDetailsFragment(Product product) {
        FragmentManager fragmentManager = getFragmentManager();
        ProductDetailsFragment productFragment = ProductDetailsFragment.newInstance(product);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container, productFragment)
                .addToBackStack(null).commit();
    }
}
