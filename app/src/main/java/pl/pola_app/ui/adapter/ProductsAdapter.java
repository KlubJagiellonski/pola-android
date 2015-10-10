package pl.pola_app.ui.adapter;

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
        viewHolder.bind(products.get(i));

        viewHolder.productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventBus.post(new ProductItemClickedEvent(viewHolder.productCard, products.get(i)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Product getItemAt(int position) {
        return products.get(position);
    }

    public void addProduct(Product product) {
        if(product.company == null) {
            return;
        }

        //always one item per company
        for(Product p : products) {
            if(p.company.id == product.company.id) {
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

        @Bind(R.id.card_view)
        CardView productCard;

        @BindString(R.string.nip)
        String nipString;

        @BindString(R.string.pl_capital)
        String plCapitalString;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Product product) {
            companyName.setText(product.company.name);
        }
    }
}
