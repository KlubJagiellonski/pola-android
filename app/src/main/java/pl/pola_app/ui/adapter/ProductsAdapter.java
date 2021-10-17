package pl.pola_app.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import pl.pola_app.R;
import pl.pola_app.databinding.ViewProductItemBinding;
import pl.pola_app.model.SearchResult;
import timber.log.Timber;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    public interface ProductClickListener {
        void itemClicked(SearchResult searchResult);
    }

    @NonNull
    private final Context context;
    @NonNull
    private final ProductList searchResults;
    @Nullable
    private ProductClickListener productClickListener;

    public ProductsAdapter(@NonNull final Context context,
            @NonNull final ProductList searchResults) {
        this.searchResults = searchResults;
        this.context = context;
    }

    public void setOnProductClickListener(ProductClickListener productClickListener) {
        this.productClickListener = productClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewProductItemBinding binding = ViewProductItemBinding.inflate(inflater, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final SearchResult p = searchResults.get(i);
        viewHolder.bind(p);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    private void onItemClicked(int position) {
        if (productClickListener == null) {
            return;
        }

        if (position > searchResults.size()) { //TODO: is it even possible to reach this state?
            Timber.w(new IndexOutOfBoundsException(), "Position: %d, list size: %d", position, searchResults.size());
            return;
        }

        SearchResult searchResult = searchResults.get(position);
        if (searchResult != null) {
            productClickListener.itemClicked(searchResult);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ViewProductItemBinding binding;

        ViewHolder(ViewProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SearchResult searchResult) {
            itemView.setOnClickListener(this);
            if (searchResult == null) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.companyName.setText("");
                binding.scoreBar.setProgress(0);
                applyStyle(context.getString(R.string.type_white));
                return;
            }

            binding.progressBar.setVisibility(View.GONE);

            applyStyle(searchResult.card_type);
            binding.companyName.setText(searchResult.name != null ? searchResult.name : searchResult.companies.get(0).name);

            if (searchResult.companies != null && searchResult.companies.get(0).plScore != null) {
                binding.scoreBar.setProgress(searchResult.companies.get(0).plScore);
            } else {
                binding.scoreBar.setProgress(0);
            }

            if (searchResult.companies != null && searchResult.companies.get(0).is_friend != null && searchResult.companies.get(
                    0).is_friend) {
                binding.heartImage.setVisibility(View.VISIBLE);
            } else {
                binding.heartImage.setVisibility(View.GONE);
            }
        }

        private void applyStyle(String style) {
            final Resources resources = context.getResources();
            if (style.equals(resources.getString(R.string.type_grey))) {
                binding.productCard.setCardBackgroundColor(resources.getColor(R.color.card_type_grey_bk));
                binding.scoreBar.setBackgroundColor(resources.getColor(R.color.card_type_grey_score_bk));
            } else {
                binding.productCard.setCardBackgroundColor(resources.getColor(R.color.card_type_white_bk));
                binding.scoreBar.setBackgroundColor(resources.getColor(R.color.card_type_white_score_bk));
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onItemClicked(position);
            }
        }
    }
}
