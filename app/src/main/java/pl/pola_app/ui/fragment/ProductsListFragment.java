package pl.pola_app.ui.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.parceler.Parcel;
import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.LinearLayoutManager;
import pl.pola_app.model.Product;
import pl.pola_app.ui.activity.ProductDetailsActivity;
import pl.pola_app.ui.adapter.ProductsAdapter;
import pl.pola_app.ui.event.CardClickedEvent;

public class ProductsListFragment extends Fragment {

    @Bind(R.id.products_list)
    RecyclerView productsList;

    @Inject
    ProductsAdapter productsAdapter;

    @Inject
    Bus eventBus;

    public ProductsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View productsListView = inflater.inflate(R.layout.fragment_products_list, container, false);
        PolaApplication.component(getActivity()).inject(this);
        ButterKnife.bind(this, productsListView);
        eventBus.register(this);
        return productsListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        productsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        productsList.setAdapter(productsAdapter);

        if(savedInstanceState != null) {
            productsAdapter.onRestoreInstanceSate(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        productsAdapter.onSaveInstanceState(outState);
    }

    public void addProduct(Product product) {
        productsAdapter.addProduct(product);
    }

    @Subscribe
    public void cardClicked(CardClickedEvent event) {
        // Ordinary Intent for launching a new activity
        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
        intent.putExtra(Product.class.getName(), Parcels.wrap(productsAdapter.getItemAt(event.itemPosition)));

        // Get the transition name from the string
        String transitionName = getString(R.string.transition_product_details);

        // Define the view that the animation will start from
        View viewStart = event.productCard;

        ActivityOptionsCompat options =

                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        viewStart,   // Starting view
                        transitionName    // The String
                );
        //Start the Intent
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }
}
