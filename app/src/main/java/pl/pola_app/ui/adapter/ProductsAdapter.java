package pl.pola_app.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.model.Product;

public class ProductsAdapter extends android.support.v7.widget.RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    public interface ProductClickListener {
        void itemClicked(Product product);
    }

    private final List<Product> products;
    private ProductClickListener productClickListener;

    public ProductsAdapter(List<Product> products) {
        this.products = products;
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
        final Product p = products.get(i);
        viewHolder.bind(p);

        if (p != null) {
            viewHolder.productCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productClickListener.itemClicked(p);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.company_name)
        TextView companyName;

        @Bind(R.id.score_bar)
        ProgressBar plScore;

        @Bind(R.id.view_product_item)
        CardView productCard;

        @Bind(R.id.progressBar)
        ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Product product) {
            productCard.setCardBackgroundColor(Color.WHITE);
            plScore.setBackgroundColor(Color.parseColor("#CCCCCC"));

            if (product == null) {
                progress.setVisibility(View.VISIBLE);
                companyName.setText("");
                plScore.setProgress(0);
                return;
            }

            progress.setVisibility(View.GONE);

            if (product.verified == false) {
                productCard.setCardBackgroundColor(Color.parseColor("#E9E8E7"));
                plScore.setBackgroundColor(Color.parseColor("#666666"));
            }

            if (product.plScore != null) {
                plScore.setProgress(product.plScore);
            } else {
                plScore.setProgress(0);
            }

            if (product.company != null && product.company.name != null) {
                companyName.setText(product.company.name);
            } else {
                companyName.setText(R.string.unknown_company);
            }
        }
    }
}
