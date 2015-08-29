package pl.pola_app.ui.fragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.pola_app.R;
import pl.pola_app.model.Product;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailsFragment extends DialogFragment {
    private static final String ARG_PRODUCT = "product";
    public static final String TAG = ProductDetailsFragment.class.getName();

    private Product product;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }
}
