package pl.pola_app.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;

public class ProductDetailsFragment extends DetailsFragment {

    @BindView(R.id.buttonWorkers)
    ImageButton buttonWorkers;

    @BindView(R.id.buttonGlobent)
    ImageButton buttonGlobent;

    @BindView(R.id.buttonRegistered)
    ImageButton buttonRegistered;

    @BindView(R.id.buttonRnd)
    ImageButton buttonRnd;

    @BindView(R.id.plcapital_details_progressbar)
    ProgressBar plCapitalBar;

    @BindView(R.id.plcapital_details_text)
    TextView plCapitalScoreText;


    public static ProductDetailsFragment newInstance(SearchResult searchResult) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(SearchResult.class.getName(), Parcels.wrap(searchResult));
        fragment.setArguments(args);
        return fragment;
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

        tv_companyName.setText(searchResult.name != null ? searchResult.name : searchResult.companies.get(0).name);

        if (searchResult.companies != null && searchResult.companies.get(0).plScore != null) {
            plScoreBar.setProgress(searchResult.companies.get(0).plScore);
            plScoreText.setText(searchResult.companies.get(0).plScore + getString(R.string.pt));
        } else {
            plScoreBar.setProgress(0);
            plScoreText.setText("?");
        }

        if (searchResult.companies != null && searchResult.companies.get(0).plCapital != null) {
            plCapitalBar.setProgress(searchResult.companies.get(0).plCapital);
            plCapitalScoreText.setText(searchResult.companies.get(0).plCapital + "%");
        } else {
            plCapitalBar.setProgress(0);
            plCapitalScoreText.setText("?");
        }

        if (searchResult.altText != null) {
            plDataLayout.setVisibility(View.GONE);
            altText.setVisibility(View.VISIBLE);
            altText.setText(searchResult.altText);
        } else {
            altText.setVisibility(View.GONE);
            plDataLayout.setVisibility(View.VISIBLE);

            if (searchResult.companies.get(0).plWorkers != null && searchResult.companies.get(0).plWorkers != 0) {
                buttonWorkers.setSelected(true);
            } else if (searchResult.companies.get(0).plWorkers == null) {
                buttonWorkers.setEnabled(false);
            }

            if (searchResult.companies.get(0).plRnD != null && searchResult.companies.get(0).plRnD != 0) {
                buttonRnd.setSelected(true);
            } else if (searchResult.companies.get(0).plRnD == null) {
                buttonRnd.setEnabled(false);
            }

            if (searchResult.companies.get(0).plRegistered != null && searchResult.companies.get(0).plRegistered != 0) {
                buttonRegistered.setSelected(true);
            } else if (searchResult.companies.get(0).plRegistered == null) {
                buttonRegistered.setEnabled(false);
            }

            if (searchResult.companies.get(0).plNotGlobEnt != null && searchResult.companies.get(0).plNotGlobEnt != 0) {
                buttonGlobent.setSelected(true);
            } else if (searchResult.companies.get(0).plNotGlobEnt == null) {
                buttonGlobent.setEnabled(false);
            }

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

        if (searchResult.companies != null && searchResult.companies.get(0).is_friend != null && searchResult.companies.get(0).is_friend && searchResult.friend_text != null) {
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
}
