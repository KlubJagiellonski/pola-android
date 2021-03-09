package pl.pola_app.ui.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.squareup.otto.Bus;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pl.pola_app.R;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.activity.ActivityWebView;
import pl.pola_app.ui.delegate.DetailsFragmentDelegate;
import pl.pola_app.ui.event.ReportButtonClickedEvent;

public abstract class DetailsFragment extends DialogFragment {

    @BindView(R.id.product_info_card)
    CardView productInfoCard;

    @BindView(R.id.main_company_name)
    TextView tv_companyName;

    @BindView(R.id.main_plscore_details_progressbar)
    ProgressBar plScoreBar;

    @BindView(R.id.main_plscore_details_text)
    TextView plScoreText;

    @BindView(R.id.message)
    TextView reportMessage;

    @BindView(R.id.seePolaFriends)
    Button seePolaFriendsButton;

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

    protected SearchResult searchResult;

    protected DetailsFragmentDelegate delegate;

    private EventLogger logger;
    private SessionId sessionId;


    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsFragmentDelegate) {
            delegate = (DetailsFragmentDelegate) context;
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
    public void onDestroy() {
        super.onDestroy();
        delegate = null;
    }

    protected void applyStyle(String cardType, String reportType) {
        if (cardType.equals(resources.getString(R.string.type_grey))) {
            productInfoCard.setCardBackgroundColor(resources.getColor(R.color.card_type_grey_bk));
        } else {
            productInfoCard.setCardBackgroundColor(resources.getColor(R.color.card_type_white_bk));
        }

        if (reportType.equals(resources.getString(R.string.type_red))) {
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
        bundle.putString("item", getString(R.string.polas_friends));
        bundle.putString("device_id", sessionId.get());
        logger.logCustom("product_details_friend", bundle);

        Intent intent = new Intent(getActivity(), ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_FRIENDS);
        startActivity(intent);
    }
}