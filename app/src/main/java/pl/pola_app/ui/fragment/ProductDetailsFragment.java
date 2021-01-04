package pl.pola_app.ui.fragment;


import android.os.Bundle;
import android.view.View;

import org.parceler.Parcels;

import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;

public class ProductDetailsFragment extends DetailsFragment {

    public static ProductDetailsFragment newInstance(SearchResult searchResult) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(SearchResult.class.getName(), Parcels.wrap(searchResult));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Hide LIDL elements:
        companyCapitalInfo2.setVisibility(View.GONE);

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
            plCapitalBar1.setProgress(searchResult.plCapital);
            plCapitalText1.setText(searchResult.plCapital + "%");
        } else {
            plCapitalBar1.setProgress(0);
            plCapitalText1.setText("?");
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
            seePolaFriendsButton.setVisibility(View.VISIBLE);
            seePolaFriendsButton.setOnClickListener((view) ->{
                if(delegate != null)
                    delegate.onsSeePolaFriendsAction();
            });
        }else {
            seePolaFriendsButton.setVisibility(View.GONE);
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


}
