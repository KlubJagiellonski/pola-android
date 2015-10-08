package pl.pola_app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.ui.event.ProductRequestSuccessEvent;
import pl.pola_app.ui.fragment.ProductsListFragment;


public class MainActivity extends AppCompatActivity {

    @Inject
    Bus eventBus;

    private ProductsListFragment productsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this, this);
        PolaApplication.component(this).inject(this);
        eventBus.register(this);

        productsListFragment = (ProductsListFragment) getFragmentManager().findFragmentById(R.id.product_list_fragment);
    }

    @Subscribe
    public void productRequestSuccess(ProductRequestSuccessEvent event) {
        productsListFragment.addProduct(event.getProduct());
    }
}
