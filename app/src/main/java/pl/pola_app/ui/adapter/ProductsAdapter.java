package pl.pola_app.ui.adapter;

import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.model.Product;

/**
 * Created by grzegorzkapusta on 02.10.2015.
 */
public class ProductsAdapter extends android.support.v7.widget.RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private final static String PRODUCTS = "Products";
    private List<Product> products;

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
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bind(products.get(i));
    }

    @Override
    public int getItemCount() {
        return products.size();
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
        outState.putParcelable(PRODUCTS, Parcels.wrap(products));
    }

    public void onRestoreInstanceSate(Bundle savedInstanceState) {
        products = Parcels.unwrap(savedInstanceState.getParcelable(PRODUCTS));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.company_name)
        TextView companyName;

        @Bind(R.id.company_address)
        TextView companyAddress;

        @Bind(R.id.company_nip)
        TextView companyNip;

        @Bind(R.id.company_plCapital)
        TextView companyPlCapital;

        @Bind(R.id.company_plCapital_notes)
        TextView companyPlCapitalNotes;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Product product) {
            companyName.setText(product.company.name);
            companyAddress.setText(product.company.address);
            companyNip.setText("NIP: " + product.company.nip);
            companyPlCapital.setText("Udział polskiego kapitału: " + product.company.plCapital + "%");
            companyPlCapitalNotes.setText(product.company.plCapital_notes);
        }
    }
}
