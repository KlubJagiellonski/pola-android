package pl.pola_app.ui.fragment;


import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.otto.Bus;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.model.Product;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;
import pl.pola_app.ui.event.ReportButtonClickedEvent;

public class ProductDetailsFragment extends DialogFragment {

    @Bind(R.id.product_info_card)
    CardView productInfoCard;

    @Bind(R.id.company_name)
    TextView tv_companyName;

    @Bind(R.id.plscore_details_progressbar)
    ProgressBar plScoreBar;

    @Bind(R.id.plscore_details_text)
    TextView plScoreText;

    @Bind(R.id.plcapital_details_progressbar)
    ProgressBar plCapitalBar;

    @Bind(R.id.plcapital_details_text)
    TextView plCapitalText;

    @Bind(R.id.pl_workers_radiobutton)
    RadioButton plWorkersRadio;

    @Bind(R.id.pl_rnd_radiobutton)
    RadioButton plRnDRadio;

    @Bind(R.id.pl_registered_radiobutton)
    RadioButton plRegisteredRadio;

    @Bind(R.id.pl_globent_radiobutton)
    RadioButton plGlobEntRadio;

    @Bind(R.id.message)
    TextView reportMessage;

    @Bind(R.id.report_button)
    Button reportButton;

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
            productInfoCard.setCardBackgroundColor(Color.parseColor("#E9E8E7"));
            reportMessage.setText(R.string.not_verified_report_message);
            reportButton.setText(R.string.send_report);
            reportButton.setBackgroundColor(Color.parseColor("#D8002F"));
            reportButton.setTextColor(Color.WHITE);
        } else {
            productInfoCard.setCardBackgroundColor(Color.WHITE);
            reportMessage.setText(R.string.report_message);
            reportButton.setText(R.string.report_button_text);
            reportButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
            reportButton.setTextColor(Color.parseColor("#D8002F"));
        }

        if(product.company.name != null) {
            tv_companyName.setText(product.company.name);
        }

        if(product.plScore != null) {
            plScoreBar.setProgress(product.plScore);
            plScoreText.setText(product.plScore + "%");
        } else {
            plScoreBar.setProgress(0);
            plScoreText.setText("?");
        }

        if(product.company.plCapital != null) {
            plCapitalBar.setProgress(product.company.plCapital);
            plCapitalText.setText(product.company.plCapital + "%");
        } else {
            plCapitalBar.setProgress(0);
            plCapitalText.setText("?");
        }

        if (product.company.plWorkers != null && product.company.plWorkers != 0) {
            plWorkersRadio.toggle();
        }

        if (product.company.plRnD != null && product.company.plRnD != 0) {
            plRnDRadio.toggle();
        }

        if (product.company.plRegistered != null && product.company.plRegistered != 0) {
            plRegisteredRadio.toggle();
        }

        if (product.company.plNotGlobEnt != null && product.company.plNotGlobEnt != 0) {
            plGlobEntRadio.toggle();
        }

        productInfoCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                eventBus.post(new ProductDetailsFragmentDismissedEvent());
            }
        });
    }

    @OnClick(R.id.report_button)
    public void report() {
        eventBus.post(new ReportButtonClickedEvent(product));
    }
}
