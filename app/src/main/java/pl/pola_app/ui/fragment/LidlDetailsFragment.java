package pl.pola_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;

public class LidlDetailsFragment extends DetailsFragment {

    @BindView(R.id.plcapital_details_progressbar_2)
    ProgressBar plCapitalBar2;

    @BindView(R.id.company_plCapital_percent2)
    TextView plCapitalText2;

    @BindView(R.id.plcapital_details_text_2)
    TextView plCapitalScoreText2;

    @BindView(R.id.company_plCapital_info2)
    LinearLayout companyCapitalInfo2;

    public static LidlDetailsFragment newInstance(SearchResult searchResult) {
        LidlDetailsFragment fragment = new LidlDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(SearchResult.class.getName(), Parcels.wrap(searchResult));
        fragment.setArguments(args);
        return fragment;
    }

    public LidlDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details_lidl, container, false);
        PolaApplication.component(getActivity()).inject(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


//        companyCapitalInfo2.setVisibility(View.VISIBLE);
//        companyButtons.setVisibility(View.GONE);

        applyStyle(searchResult.card_type, searchResult.report.button_type);
        reportMessage.setText(searchResult.report.text);
        reportButton.setText(searchResult.report.button_text);

        tv_companyName.setText(searchResult.companies.get(1).name + " / " + searchResult.companies.get(0).name );
        plCapitalText1.setText(searchResult.companies.get(1).name);
        plCapitalText2.setText(searchResult.companies.get(0).name );


        plScoreBar.setProgress(0);
        plScoreText.setText("?");

        if (searchResult.companies.get(1).plScore != null) {
            plCapitalBar1.setProgress(searchResult.companies.get(1).plScore);
            plCapitalScoreText1.setText(searchResult.companies.get(1).plScore + getString(R.string.pt));
        } else {
            plCapitalBar1.setProgress(0);
            plCapitalScoreText1.setText("?");
        }

        if (searchResult.companies.get(0).plScore != null) {
            plCapitalBar2.setProgress(searchResult.companies.get(0).plScore);
            plCapitalScoreText2.setText(searchResult.companies.get(0).plScore + getString(R.string.pt));
        } else {
            plCapitalBar2.setProgress(0);
            plCapitalScoreText2.setText("?");
        }


        /////////////////

        if (searchResult.altText != null) {
            plDataLayout.setVisibility(View.GONE);
            altText.setVisibility(View.VISIBLE);
            altText.setText(searchResult.altText);
        } else {
            altText.setVisibility(View.GONE);
            plDataLayout.setVisibility(View.VISIBLE);

            if (searchResult.companies.get(0).description != null) {
                description.setVisibility(View.VISIBLE);
                description.setText(searchResult.companies.get(0).description);
            } else {
                description.setVisibility(View.GONE);
            }
        }

        if (searchResult.askForSupport()) {
            seePolaFriendsButton.setVisibility(View.VISIBLE);
            seePolaFriendsButton.setOnClickListener((view) -> {
                if (delegate != null)
                    delegate.onsSeePolaFriendsAction();
            });
        } else {
            seePolaFriendsButton.setVisibility(View.GONE);
        }

        if (searchResult.companies.get(0).is_friend != null && searchResult.companies.get(0).is_friend && searchResult.friend_text != null) {
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
}
