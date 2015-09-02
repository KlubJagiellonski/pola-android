package pl.pola_app.ui.fragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.model.Product;

public class ProductDetailsFragment extends DialogFragment {
    private static final String ARG_PRODUCT = "product";

    private Product product;

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

    public static ProductDetailsFragment newInstance(Product product) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable(ARG_PRODUCT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View productDetailsView = inflater.inflate(R.layout.fragment_product_details, container, false);
        ButterKnife.bind(this, productDetailsView);

        companyName.setText(product.company.name);
        companyAddress.setText(product.company.address);
        companyNip.setText("NIP: " + product.company.nip);
        companyPlCapital.setText("Udział polskiego kapitału: " + product.company.plCapital + "%");
        companyPlCapitalNotes.setText(product.company.plCapital_notes);

        return productDetailsView;
    }
}
