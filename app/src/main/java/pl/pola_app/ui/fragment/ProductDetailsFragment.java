package pl.pola_app.ui.fragment;


import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.activity.ActivityWebView;
import pl.pola_app.ui.delegate.ProductDetailsFragmentDelegate;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;
import pl.pola_app.ui.event.ReportButtonClickedEvent;

public class ProductDetailsFragment extends DialogFragment {

    @BindView(R.id.product_info_card)
    CardView productInfoCard;

    @BindView(R.id.company_name)
    TextView tv_companyName;

    @BindView(R.id.plscore_details_progressbar)
    ProgressBar plScoreBar;

    @BindView(R.id.plscore_details_text)
    TextView plScoreText;

    @BindView(R.id.plcapital_details_progressbar)
    ProgressBar plCapitalBar;

    @BindView(R.id.plcapital_details_text)
    TextView plCapitalText;

    @BindView(R.id.buttonWorkers)
    ImageButton buttonWorkers;

    @BindView(R.id.buttonGlobent)
    ImageButton buttonGlobent;

    @BindView(R.id.buttonRegistered)
    ImageButton buttonRegistered;

    @BindView(R.id.buttonRnd)
    ImageButton buttonRnd;

    @BindView(R.id.message)
    TextView reportMessage;

    @BindView(R.id.teach_pola_button)
    Button teachPolaButton;

    @BindView(R.id.report_button)
    Button reportButton;

    @BindView(R.id.tv_altText)
    TextView altText;

    @BindView(R.id.tv_description)
    TextView description;

    @BindView(R.id.pl_data_layout)
    LinearLayout plDataLayout;

    @BindView(R.id.isFriendLayout)
    LinearLayout isFriendLayout;

    @BindView(R.id.isFriendText)
    TextView isFriendText;

    @Inject
    Bus eventBus;

    @Inject
    Resources resources;

    private SearchResult searchResult;

    private ProductDetailsFragmentDelegate delegate;

    private EventLogger logger;
    private SessionId sessionId;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ProductDetailsFragmentDelegate) {
            delegate = (ProductDetailsFragmentDelegate) context;
            return;
        }
        throw new IllegalArgumentException("Context that uses this fragment should implements ProductDetailsFragmentDelegate class");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchResult = Parcels.unwrap(getArguments().getParcelable(SearchResult.class.getName()));
        }

        sessionId = SessionId.create(getActivity());
        logger = new EventLogger(getActivity());
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

            if (searchResult.plRnD != null && searchResult.plRnD != 0) {
                buttonRnd.setSelected(true);
            } else if (searchResult.plRnD == null) {
                buttonRnd.setEnabled(false);
            }

            if (searchResult.plRegistered != null && searchResult.plRegistered != 0) {
                buttonRegistered.setSelected(true);
            } else if (searchResult.plRegistered == null) {
                buttonRegistered.setEnabled(false);
            }

            if (searchResult.plNotGlobEnt != null && searchResult.plNotGlobEnt != 0) {
                buttonGlobent.setSelected(true);
            } else if (searchResult.plNotGlobEnt == null) {
                buttonGlobent.setEnabled(false);
            }

            if(searchResult.description != null) {
                description.setVisibility(View.VISIBLE);
                description.setText(searchResult.description);
            } else {
                description.setVisibility(View.GONE);
            }
        }

        if(searchResult.askForSupport()) {
            teachPolaButton.setText(searchResult.donate.title);
            teachPolaButton.setVisibility(View.VISIBLE);
            teachPolaButton.setOnClickListener((view) ->{
                if(delegate != null)
                delegate.onSupportPolaAction(searchResult);
            });
        }else {
            teachPolaButton.setVisibility(View.GONE);
        }

        if(searchResult.is_friend != null && searchResult.is_friend && searchResult.friend_text != null) {
            isFriendLayout.setVisibility(View.VISIBLE);
            isFriendText.setText(searchResult.friend_text);
        }

        productInfoCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                eventBus.post(new ProductDetailsFragmentDismissedEvent());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        delegate = null;
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

    @OnClick(R.id.isFriendLayout)
    void onFriendsClick() {
        Bundle bundle = new Bundle();
        bundle.putString("item", "Przyjaciele Poli");
        bundle.putString("device_id", sessionId.get());
        logger.logCustom("product_details_friend", bundle);

        Intent intent = new Intent(getActivity(), ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_FRIENDS);
        startActivity(intent);
    }
}
