package pl.pola_app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.model.Product;
import pl.pola_app.ui.event.ProductItemClickedEvent;
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

    @Subscribe
    public void productItemClickedEvent(ProductItemClickedEvent event) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(Product.class.getName(), Parcels.wrap(event.productItem));

        // Get the transition name from the string
        String transitionName = getString(R.string.transition_product_details);

        // Define the view that the animation will start from
        View viewStart = event.productCard;

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        viewStart,
                        transitionName
                );
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
