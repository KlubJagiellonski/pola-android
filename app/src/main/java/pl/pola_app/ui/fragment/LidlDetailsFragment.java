package pl.pola_app.ui.fragment;

import android.os.Bundle;
import android.view.View;

import org.parceler.Parcels;

import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;

public class LidlDetailsFragment extends DetailsFragment {

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        companyCapitalInfo2.setVisibility(View.VISIBLE);
        companyButtons.setVisibility(View.GONE);

        applyStyle(searchResult.card_type, searchResult.report_button_type);
        reportMessage.setText(searchResult.report_text);
        reportButton.setText(searchResult.report_button_text);

        tv_companyName.setText(searchResult.name + " / " + "<second company>");
        plCapitalText1.setText(searchResult.name);
        plCapitalText2.setText("<second company>");


        plScoreBar.setProgress(0);
        plScoreText.setText("?");

        if (searchResult.plScore != null) {
            plCapitalBar1.setProgress(searchResult.plScore);
            plCapitalScoreText1.setText(searchResult.plScore + " pkt");
        } else {
            plCapitalBar1.setProgress(0);
            plCapitalScoreText1.setText("?");
        }

        if (searchResult.plScore != null) {
            plCapitalBar2.setProgress(0);
            plCapitalScoreText2.setText("<second company score>" + " pkt");
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

            if (searchResult.description != null) {
                description.setVisibility(View.VISIBLE);
                description.setText(searchResult.description);
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

        if (searchResult.is_friend != null && searchResult.is_friend && searchResult.friend_text != null) {
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
