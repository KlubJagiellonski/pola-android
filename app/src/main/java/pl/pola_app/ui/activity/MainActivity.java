package pl.pola_app.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;
import pl.pola_app.ui.event.ProductItemClickedEvent;
import pl.pola_app.ui.event.ProductRequestSuccessEvent;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
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

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void productRequestSuccess(ProductRequestSuccessEvent event) {
        productsListFragment.addProduct(event.getProduct());
    }

    @Subscribe
    public void productItemClicked(ProductItemClickedEvent event) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in, 0, 0, R.animator.slide_out);
        ProductDetailsFragment newFragment = ProductDetailsFragment.newInstance(event.product);
        ft.add(R.id.container, newFragment, ProductDetailsFragment.class.getName());
        ft.hide(productsListFragment);
        ft.addToBackStack(ProductDetailsFragment.class.getName());
        ft.commit();
    }

    @Subscribe
    public void productDetailsFragmentDismissed(ProductDetailsFragmentDismissedEvent event) {
        getFragmentManager().popBackStack(ProductDetailsFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
