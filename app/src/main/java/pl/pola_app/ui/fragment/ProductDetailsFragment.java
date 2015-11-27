package pl.pola_app.ui.fragment;


import android.app.DialogFragment;
import android.content.res.Resources;
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
import pl.pola_app.model.SearchResult;
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

    @Bind(R.id.tv_pl_capital_notes)
    TextView plCapitalNotesTextView;

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

    @Bind(R.id.tv_pl_workers_notes)
    TextView plWorkersTextView;

    @Bind(R.id.tv_pl_rnd_notes)
    TextView plRndTextView;

    @Bind(R.id.tv_pl_registered_notes)
    TextView plRegisteredTextView;

    @Bind(R.id.tv_pl_globent_notes)
    TextView plGlobentTextView;

    @Bind(R.id.pl_data_layout)
    LinearLayout plDataLayout;

    @Inject
    Bus eventBus;

    @Inject
    Resources resources;

    private SearchResult searchResult;

    public static ProductDetailsFragment newInstance(SearchResult searchResult) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(SearchResult.class.getName(), Parcels.wrap(searchResult));
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
            searchResult = Parcels.unwrap(getArguments().getParcelable(SearchResult.class.getName()));
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

        applyStyle(searchResult.card_type, searchResult.report_button_type);
        reportMessage.setText(searchResult.report_text);
        reportButton.setText(searchResult.report_button_text);

        tv_companyName.setText(searchResult.name);

        if(searchResult.plScore != null) {
            plScoreBar.setProgress(searchResult.plScore);
            plScoreText.setText(searchResult.plScore + " pkt");
        } else {
            plScoreBar.setProgress(0);
            plScoreText.setText("?");
        }

        if(searchResult.plCapital != null) {
            plCapitalBar.setProgress(searchResult.plCapital);
            plCapitalText.setText(searchResult.plCapital + "%");
        } else {
            plCapitalBar.setProgress(0);
            plCapitalText.setText("?");
        }

        if(searchResult.plCapital_notes != null) {
            plCapitalNotesTextView.setVisibility(View.VISIBLE);
            plCapitalNotesTextView.setText(searchResult.plCapital_notes);
        } else {
            plCapitalNotesTextView.setVisibility(View.GONE);
        }

        if(searchResult.altText != null) {
            plDataLayout.setVisibility(View.GONE);
            altText.setVisibility(View.VISIBLE);
            altText.setText(searchResult.altText);
        } else {
            altText.setVisibility(View.GONE);
            plDataLayout.setVisibility(View.VISIBLE);

            if (searchResult.plWorkers != null && searchResult.plWorkers != 0) {
                buttonWorkers.setSelected(true);
            } else if (searchResult.plWorkers == null) {
                buttonWorkers.setEnabled(false);
            }

            if(searchResult.plWorkers_notes != null) {
                plWorkersTextView.setVisibility(View.VISIBLE);
                plWorkersTextView.setText(searchResult.plWorkers_notes);
            } else {
                plWorkersTextView.setVisibility(View.GONE);
            }

            if (searchResult.plRnD != null && searchResult.plRnD != 0) {
                buttonRnd.setSelected(true);
            } else if (searchResult.plRnD == null) {
                buttonRnd.setEnabled(false);
            }

            if(searchResult.plRnD_notes != null) {
                plRndTextView.setVisibility(View.VISIBLE);
                plRndTextView.setText(searchResult.plRnD_notes);
            } else {
                plRndTextView.setVisibility(View.GONE);
            }

            if (searchResult.plRegistered != null && searchResult.plRegistered != 0) {
                buttonRegistered.setSelected(true);
            } else if (searchResult.plRegistered == null) {
                buttonRegistered.setEnabled(false);
            }

            if(searchResult.plRegistered_notes != null) {
                plRegisteredTextView.setVisibility(View.VISIBLE);
                plRegisteredTextView.setText(searchResult.plRegistered_notes);
            } else {
                plRegisteredTextView.setVisibility(View.GONE);
            }

            if (searchResult.plNotGlobEnt != null && searchResult.plNotGlobEnt != 0) {
                buttonGlobent.setSelected(true);
            } else if (searchResult.plNotGlobEnt == null) {
                buttonGlobent.setEnabled(false);
            }

            if(searchResult.plNotGlobEnt_notes != null) {
                plGlobentTextView.setVisibility(View.VISIBLE);
                plGlobentTextView.setText(searchResult.plNotGlobEnt_notes);
            } else {
                plGlobentTextView.setVisibility(View.GONE);
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
        eventBus.post(new ReportButtonClickedEvent(searchResult));
    }
}
