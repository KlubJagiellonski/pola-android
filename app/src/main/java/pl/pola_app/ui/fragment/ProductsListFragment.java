package pl.pola_app.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.otto.Bus;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.BuildConfig;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.ProductsListLinearLayoutManager;
import pl.pola_app.model.Product;
import pl.pola_app.ui.adapter.ProductsAdapter;
import pl.pola_app.ui.event.ProductItemClickedEvent;

public class ProductsListFragment extends Fragment implements ProductsAdapter.ProductClickListener {

    @Bind(R.id.products_list)
    RecyclerView productsList;

    @Inject
    ProductsListLinearLayoutManager productsListLinearLayoutManager;

    @Inject
    Bus eventBus;

    private ProductsAdapter productsAdapter;
    public List<Product> products;

    public ProductsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View productsListView = inflater.inflate(R.layout.fragment_products_list, container, false);
        PolaApplication.component(getActivity()).inject(this);
        ButterKnife.bind(this, productsListView);

        products = new ArrayList<>();
        return productsListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            products = Parcels.unwrap(savedInstanceState.getParcelable(Product.class.getName()));
        }

        productsAdapter = new ProductsAdapter(getContext(), products);
        productsAdapter.setOnProductClickListener(this);

        productsList.setLayoutManager(productsListLinearLayoutManager);
        productsList.setAdapter(productsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Product.class.getName(), Parcels.wrap(products));
    }

    public void addProduct(Product product) {
        if (products.size() > 0 && products.get(0) == null) {
            if(BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("addProduct")
                        .putCustomAttribute("good", "true"));
            }
            products.set(0, product);
        } else {
            if(BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("addProduct")
                    .putCustomAttribute("good", "false"));
            }
            products.add(0, product);
        }
        productsAdapter.notifyDataSetChanged();
    }

    public void createProductPlaceholder() {
        products.add(0, null);
        productsAdapter.notifyDataSetChanged();
    }

    public void removeProductPlaceholder() {
        if(products.size() > 0 && products.get(0) == null) {
            products.remove(0);
            productsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void itemClicked(Product product) {
        eventBus.post(new ProductItemClickedEvent(product));
    }

    public boolean itemExists(String code) {
        for(Product p : products) {
            if(p != null) {
                if (p.code.equals(code)) {
                    products.remove(p);
                    products.add(0, p);
                    productsAdapter.notifyDataSetChanged();
                    return true;
                }
            }
        }

        return false;
    }
}
