package pl.pola_app.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.ProductsListLinearLayoutManager;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.adapter.OnProductListChanged;
import pl.pola_app.ui.adapter.ProductList;
import pl.pola_app.ui.adapter.ProductsAdapter;
import pl.pola_app.ui.event.ProductItemClickedEvent;

public class ProductsListFragment extends Fragment implements ProductsAdapter.ProductClickListener {

    @Bind(R.id.products_list)
    RecyclerView productsListView;

    @Inject
    ProductsListLinearLayoutManager productsListLinearLayoutManager;

    @Inject
    Bus eventBus;

    private ProductsAdapter productsAdapter;
    private ProductList productList;

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

        productList = ProductList.create(savedInstanceState);

        productsAdapter = new ProductsAdapter(getActivity(), productList);
        productsAdapter.setOnProductClickListener(this);
        productList.setOnProductListChanged(new OnProductListChanged() {
            @Override
            public void onChanged() {
                productsAdapter.notifyDataSetChanged();
            }
        });

        productsListView.setLayoutManager(productsListLinearLayoutManager);
        productsListView.setAdapter(productsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        productList.writeToBundle(outState);
    }

    public void addProduct(SearchResult searchResult) {
        productList.addProduct(searchResult);
    }

    public void createProductPlaceholder() {
        productList.createProductPlaceholder();
    }

    public void removeProductPlaceholder() {
        productList.removeProductPlaceholder();
    }

    @Override
    public void itemClicked(SearchResult searchResult) {
        eventBus.post(new ProductItemClickedEvent(searchResult));
    }

    public boolean itemExists(String code) {
        return productList.itemExists(code);
    }
}
