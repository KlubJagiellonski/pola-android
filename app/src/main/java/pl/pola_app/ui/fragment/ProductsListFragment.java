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
import pl.pola_app.model.SearchResult;
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
    public List<SearchResult> searchResults;

    public ProductsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View productsListView = inflater.inflate(R.layout.fragment_products_list, container, false);
        PolaApplication.component(getActivity()).inject(this);
        ButterKnife.bind(this, productsListView);

        searchResults = new ArrayList<>();
        return productsListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            searchResults = Parcels.unwrap(savedInstanceState.getParcelable(SearchResult.class.getName()));
        }

        productsAdapter = new ProductsAdapter(getActivity(), searchResults);
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
        outState.putParcelable(SearchResult.class.getName(), Parcels.wrap(searchResults));
    }

    public void addProduct(SearchResult searchResult) {
        if (searchResults.size() > 0 && searchResults.get(0) == null) {
            if(BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("addProduct")
                        .putCustomAttribute("good", "true"));
            }
            searchResults.set(0, searchResult);
        } else {
            if(BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("addProduct")
                    .putCustomAttribute("good", "false"));
            }
            searchResults.add(0, searchResult);
        }
        productsAdapter.notifyDataSetChanged();
    }

    public void createProductPlaceholder() {
        searchResults.add(0, null);
        productsAdapter.notifyDataSetChanged();
    }

    public void removeProductPlaceholder() {
        if(searchResults.size() > 0 && searchResults.get(0) == null) {
            searchResults.remove(0);
            productsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void itemClicked(SearchResult searchResult) {
        eventBus.post(new ProductItemClickedEvent(searchResult));
    }

    public boolean itemExists(String code) {
        for(SearchResult p : searchResults) {
            if(p != null) {
                if (p.code.equals(code)) {
                    searchResults.remove(p);
                    searchResults.add(0, p);
                    productsAdapter.notifyDataSetChanged();
                    return true;
                }
            }
        }

        return false;
    }
}
