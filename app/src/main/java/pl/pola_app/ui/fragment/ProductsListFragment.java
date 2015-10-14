package pl.pola_app.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.ProductsListLinearLayoutManager;
import pl.pola_app.model.Product;
import pl.pola_app.ui.adapter.ProductsAdapter;

public class ProductsListFragment extends Fragment {

    @Bind(R.id.products_list)
    RecyclerView productsList;

    @Inject
    ProductsAdapter productsAdapter;

    @Inject
    ProductsListLinearLayoutManager productsListLinearLayoutManager;

    public ProductsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View productsListView = inflater.inflate(R.layout.fragment_products_list, container, false);
        PolaApplication.component(getActivity()).inject(this);
        ButterKnife.bind(this, productsListView);
        return productsListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        productsList.setLayoutManager(productsListLinearLayoutManager);
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
}
