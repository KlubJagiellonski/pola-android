package pl.pola_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @BindView(R.id.first_company_name)
    TextView firstCompanyName;

    @BindView(R.id.first_company_progressbar)
    ProgressBar fistCompanyProgressbar;

    @BindView(R.id.first_company_details_text)
    TextView firstCompanyScoreText;

    @BindView(R.id.second_company_name)
    TextView secondCompanyName;

    @BindView(R.id.second_company_progressbar)
    ProgressBar secondCompanyProgressbar;

    @BindView(R.id.second_company_details_text)
    TextView secondCompanyScoreText;

    public static LidlDetailsFragment newInstance(SearchResult searchResult) {
        LidlDetailsFragment fragment = new LidlDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(SearchResult.class.getName(), Parcels.wrap(searchResult));
        fragment.setArguments(args);
        return fragment;
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

        tv_companyName.setText(searchResult.companies.get(1).name + " / " + searchResult.companies.get(0).name);
        firstCompanyName.setText(searchResult.companies.get(1).name);
        secondCompanyName.setText(searchResult.companies.get(0).name);


        plScoreBar.setProgress(0);
        plScoreText.setText("?");

        if (searchResult.companies.get(1).plScore != null) {
            fistCompanyProgressbar.setProgress(searchResult.companies.get(1).plScore);
            firstCompanyScoreText.setText(searchResult.companies.get(1).plScore + getString(R.string.pt));
        } else {
            fistCompanyProgressbar.setProgress(0);
            firstCompanyScoreText.setText("?");
        }

        if (searchResult.companies.get(0).plScore != null) {
            secondCompanyProgressbar.setProgress(searchResult.companies.get(0).plScore);
            secondCompanyScoreText.setText(searchResult.companies.get(0).plScore + getString(R.string.pt));
        } else {
            secondCompanyProgressbar.setProgress(0);
            secondCompanyScoreText.setText("?");
        }

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

        productInfoCard.setOnClickListener(v -> eventBus.post(new ProductDetailsFragmentDismissedEvent()));
    }
}
