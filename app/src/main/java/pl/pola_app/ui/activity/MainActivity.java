package pl.pola_app.ui.activity;

import android.app.FragmentManager;
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

        eventBus.register(this);

        showScannerFragment();
    }

    private void showScannerFragment() {
        ScannerFragment scannerFragment = ScannerFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.container, scannerFragment).commit();
    }

    @Subscribe
    public void productRequestSuccess(ProductRequestSuccessEvent event) {
        ProductDetailsFragment productFragment = ProductDetailsFragment.newInstance(event.getProduct());
        productFragment.show(getFragmentManager(), ProductDetailsFragment.TAG);
    }
}
