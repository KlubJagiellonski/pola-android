package pl.pola_app.ui.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import pl.pola_app.BuildConfig;
import pl.pola_app.model.SearchResult;

public class ProductList {
    @NonNull private final List<SearchResult> searchResults;
    @Nullable private OnProductListChanged onProductListChanged;

    public static ProductList create(@Nullable Bundle bundle) {
        final List<SearchResult> searchResults;
        if (bundle != null) {
            searchResults = Parcels.unwrap(bundle.getParcelable(SearchResult.class.getName()));
        } else {
            searchResults = new ArrayList<>();
        }

        return new ProductList(searchResults);
    }

    private ProductList(@NonNull final List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public void setOnProductListChanged(@Nullable OnProductListChanged onProductListChanged) {
        this.onProductListChanged = onProductListChanged;
    }


    public void writeToBundle(@NonNull final Bundle bundle) {
        bundle.putParcelable(SearchResult.class.getName(), Parcels.wrap(searchResults));
    }

    public void addProduct(SearchResult searchResult) {
        if (searchResults.size() > 0 && searchResults.get(0) == null) {
            if (BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("addProduct")
                        .putCustomAttribute("good", "true"));
            }
            searchResults.set(0, searchResult);
        } else {
            if (BuildConfig.USE_CRASHLYTICS) {
                Answers.getInstance().logCustom(new CustomEvent("addProduct")
                        .putCustomAttribute("good", "false"));
            }
            searchResults.add(0, searchResult);
        }
        notifyOnChanged();
    }

    public void createProductPlaceholder() {
        searchResults.add(0, null);
        notifyOnChanged();
    }

    public void removeProductPlaceholder() {
        if (searchResults.size() > 0 && searchResults.get(0) == null) {
            searchResults.remove(0);
            notifyOnChanged();
        }
    }

    public boolean itemExists(String code) {
        for (SearchResult p : searchResults) {
            if (p != null) {
                if (p.code.equals(code)) {
                    searchResults.remove(p);
                    searchResults.add(0, p);
                    notifyOnChanged();
                    return true;
                }
            }
        }

        return false;
    }

    public int size() {
        return searchResults.size();
    }

    public SearchResult get(int position) {
        return searchResults.get(position);
    }

    private void notifyOnChanged() {
        if (onProductListChanged != null) {
            onProductListChanged.onChanged();
        }
    }

}
