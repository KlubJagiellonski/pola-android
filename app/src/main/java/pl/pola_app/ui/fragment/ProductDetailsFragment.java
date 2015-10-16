package pl.pola_app.ui.fragment;


import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.model.Product;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;

public class ProductDetailsFragment extends DialogFragment {

    @Bind(R.id.product_info_card)
    CardView productInfoCard;

    @Bind(R.id.company_name)
    TextView tv_companyName;

    @Bind(R.id.company_plRnD)
    TextView tv_plRnD;

    @Bind(R.id.company_plWorkers)
    TextView tv_plWorkers;

    @Bind(R.id.company_plCapital)
    TextView tv_plCapital;

    @Bind(R.id.company_plRegisteres)
    TextView tv_plRegistered;

    @Bind(R.id.company_plNotGlobEnt)
    TextView tv_plNotGlobalEnt;

    @BindString(R.string.pl_rnd_workers)
    String plRnDWorkers;

    @BindString(R.string.pl_workers)
    String plWorkers;

    @BindString(R.string.pl_capital)
    String plCapital;

    @BindString(R.string.pl_registered)
    String plTaxes;

    @BindString(R.string.pl_notGlobEnt)
    String plBrand;

    @Inject
    Bus eventBus;

    private Product product;

    public static ProductDetailsFragment newInstance(Product product) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Product.class.getName(), Parcels.wrap(product));
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
            product = Parcels.unwrap(getArguments().getParcelable(Product.class.getName()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        PolaApplication.component(getActivity()).inject(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(product.verified == false) {
            productInfoCard.setCardBackgroundColor(Color.GRAY);
        }

        tv_companyName.setText(product.company.name);

        if(product.company.plRnD != null) {
            tv_plRnD.setText(String.format(plRnDWorkers, product.company.plRnD));
        }

        if(product.company.plWorkers != null) {
            tv_plWorkers.setText(String.format(plWorkers, product.company.plWorkers));
        }

        if(product.company.plCapital != null) {
            tv_plCapital.setText(String.format(plCapital, product.company.plCapital));
        }

        if(product.company.plRegistered != null) {
            tv_plRegistered.setText(String.format(plTaxes, product.company.plRegistered));
        }

        if(product.company.plNotGlobEnt != null) {
            tv_plNotGlobalEnt.setText(String.format(plBrand, product.company.plNotGlobEnt));
        }

        productInfoCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                eventBus.post(new ProductDetailsFragmentDismissedEvent());
            }
        });
    }
}
