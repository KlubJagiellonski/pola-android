package pl.pola_app.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.model.SearchResult;
import timber.log.Timber;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    public interface ProductClickListener {
        void itemClicked(SearchResult searchResult);
    }

    @NonNull private final Context context;
    @NonNull private final ProductList searchResults;
    @Nullable private ProductClickListener productClickListener;

    public ProductsAdapter(@NonNull final Context context,
                           @NonNull final ProductList searchResults) {
        this.searchResults = searchResults;
        this.context = context;
    }

    public void setOnProductClickListener(ProductClickListener productClickListener) {
        this.productClickListener = productClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_product_item, viewGroup, false);
        return new ViewHolder(v);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.company_name)
        TextView companyName;

        @BindView(R.id.score_bar)
        ProgressBar plScore;

        @BindView(R.id.view_product_item)
        CardView productCard;

        @BindView(R.id.progressBar)
        ProgressBar progress;

        @BindView(R.id.heart_image)
        ImageView heartIcon;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(SearchResult searchResult) {
            if (searchResult == null) {
                progress.setVisibility(View.VISIBLE);
                companyName.setText("");
                plScore.setProgress(0);
                applyStyle(context.getString(R.string.type_white));
                return;
            }

            progress.setVisibility(View.GONE);

            applyStyle(searchResult.card_type);
            companyName.setText(searchResult.name);

            if (searchResult.plScore != null) {
                plScore.setProgress(searchResult.plScore);
            } else {
                plScore.setProgress(0);
            }

            if(searchResult.is_friend != null && searchResult.is_friend) {
                heartIcon.setVisibility(View.VISIBLE);
            } else {
                heartIcon.setVisibility(View.GONE);
            }
        }

        private void applyStyle(String style) {
            final Resources resources = context.getResources();
            if (style.equals(resources.getString(R.string.type_grey))) {
                productCard.setCardBackgroundColor(resources.getColor(R.color.card_type_grey_bk));
                plScore.setBackgroundColor(resources.getColor(R.color.card_type_grey_score_bk));
            } else {
                productCard.setCardBackgroundColor(resources.getColor(R.color.card_type_white_bk));
                plScore.setBackgroundColor(resources.getColor(R.color.card_type_white_score_bk));
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
