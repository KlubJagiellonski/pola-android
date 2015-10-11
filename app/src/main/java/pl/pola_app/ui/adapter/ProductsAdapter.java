package pl.pola_app.ui.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.model.Company;
import pl.pola_app.model.Product;

import pl.pola_app.ui.event.ProductItemClickedEvent;

public class ProductsAdapter extends android.support.v7.widget.RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private List<Product> products;

    @Inject
    Bus eventBus;

    @Inject
    ProductsAdapter() {
        products = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_product_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final Product p = products.get(i);
        viewHolder.bind(p);

        if(p.company != null && p.company.name != null) {
            viewHolder.productCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventBus.post(new ProductItemClickedEvent(viewHolder.productCard, p));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Product getItemAt(int position) {
        return products.get(position);
    }

    public void addProduct(Product product) {
        //always one item per company
        for(Product p : products) {
            if(p.company != null && p.company.name != null && p.company.name.equals(product.company.name)) {
                products.remove(p);
                break;
            }
        }
        products.add(0, product);
        notifyDataSetChanged();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Product.class.getName(), Parcels.wrap(products));
    }

    public void onRestoreInstanceSate(Bundle savedInstanceState) {
        products = Parcels.unwrap(savedInstanceState.getParcelable(Product.class.getName()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.company_name)
        TextView companyName;

        @Bind(R.id.pl_score)
        TextView plScore;

        @Bind(R.id.view_product_item)
        CardView productCard;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Product product) {
            if(product.company != null && product.company.name != null) {
                companyName.setText(product.company.name);
            } else {
                companyName.setText("??");
            }

            if(product.plScore != null) {
                plScore.setText(product.plScore.toString());
            } else {
                plScore.setText("?");
            }

            if(product.verified == false) {
                productCard.setCardBackgroundColor(Color.GRAY);
            }
        }
    }
}
