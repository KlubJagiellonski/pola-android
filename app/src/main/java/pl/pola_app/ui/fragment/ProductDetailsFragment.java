package pl.pola_app.ui.fragment;


import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

    @Bind(R.id.buttonWorkers)
    ImageButton buttonWorkers;

    @Bind(R.id.buttonGlobent)
    ImageButton buttonGlobent;

    @Bind(R.id.buttonRegistered)
    ImageButton buttonRegistered;

    @Bind(R.id.buttonRnd)
    ImageButton buttonRnd;

    @Bind(R.id.message)
    TextView reportMessage;

    @Bind(R.id.report_button)
    Button reportButton;

    @Bind(R.id.tv_altText)
    TextView altText;

    @Bind(R.id.pl_data_layout)
    LinearLayout plDataLayout;

    @Inject
    Bus eventBus;

    @Inject
    Resources resources;

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

        applyStyle(product.card_type, product.report_button_type);
        reportMessage.setText(product.report_text);
        reportButton.setText(product.report_button_text);

        tv_companyName.setText(product.name);

        if(product.plScore != null) {
            plScoreBar.setProgress(product.plScore);
            plScoreText.setText(product.plScore + " pkt");
        } else {
            plScoreBar.setProgress(0);
            plScoreText.setText("?");
        }

        if(product.plCapital != null) {
            plCapitalBar.setProgress(product.plCapital);
            plCapitalText.setText(product.plCapital + "%");
        } else {
            plCapitalBar.setProgress(0);
            plCapitalText.setText("?");
        }

        if(product.altText != null) {
            plDataLayout.setVisibility(View.GONE);
            altText.setVisibility(View.VISIBLE);
            altText.setText(product.altText);
        } else {
            altText.setVisibility(View.GONE);
            plDataLayout.setVisibility(View.VISIBLE);

            if (product.plWorkers != null && product.plWorkers != 0) {
                buttonWorkers.setSelected(true);
            } else if (product.plWorkers == null) {
                buttonWorkers.setEnabled(false);
            }

            if (product.plRnD != null && product.plRnD != 0) {
                buttonRnd.setSelected(true);
            } else if (product.plRnD == null) {
                buttonRnd.setEnabled(false);
            }

            if (product.plRegistered != null && product.plRegistered != 0) {
                buttonRegistered.setSelected(true);
            } else if (product.plRegistered == null) {
                buttonRegistered.setEnabled(false);
            }

            if (product.plNotGlobEnt != null && product.plNotGlobEnt != 0) {
                buttonGlobent.setSelected(true);
            } else if (product.plNotGlobEnt == null) {
                buttonGlobent.setEnabled(false);
            }
        }

        productInfoCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                eventBus.post(new ProductDetailsFragmentDismissedEvent());
            }
        });
    }

    private void applyStyle(String cardType, String reportType) {
        if (cardType.equals(resources.getString(R.string.type_grey))) {
            productInfoCard.setCardBackgroundColor(resources.getColor(R.color.card_type_grey_bk));
        } else {
            productInfoCard.setCardBackgroundColor(resources.getColor(R.color.card_type_white_bk));
        }

        if(reportType.equals(resources.getString(R.string.type_red))) {
            reportButton.setBackgroundColor(resources.getColor(R.color.card_type_red_report_bt_bk));
            reportButton.setTextColor(resources.getColor(R.color.card_type_red_report_bt_text));
        } else {
            reportButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
            reportButton.setTextColor(resources.getColor(R.color.card_type_white_report_bt_text));
        }
    }

    @OnClick(R.id.report_button)
    public void report() {
        eventBus.post(new ReportButtonClickedEvent(product));
    }
}
