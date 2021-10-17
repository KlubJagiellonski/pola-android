package pl.pola_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.databinding.FragmentProductDetailsLidlBinding;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;

public class LidlDetailsFragment extends DetailsFragment {

    FragmentProductDetailsLidlBinding binding;

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
        binding = FragmentProductDetailsLidlBinding.inflate(inflater, container, false);
        PolaApplication.component(getActivity()).inject(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.companyName.setText(searchResult.companies.get(1).name + " / " + searchResult.companies.get(0).name);
        binding.firstCompanyName.setText(searchResult.companies.get(1).name);
        binding.secondCompanyName.setText(searchResult.companies.get(0).name);

        binding.plScoreBar.setProgress(0);
        binding.plScoreText.setText("?");

        if (searchResult.companies.get(1).plScore != null) {
            binding.firstCompanyProgressbar.setProgress(searchResult.companies.get(1).plScore);
            binding.firstCompanyScoreText.setText(searchResult.companies.get(1).plScore + getString(R.string.pt));
        } else {
            binding.firstCompanyProgressbar.setProgress(0);
            binding.firstCompanyScoreText.setText("?");
        }

        if (searchResult.companies.get(0).plScore != null) {
            binding.secondCompanyProgressbar.setProgress(searchResult.companies.get(0).plScore);
            binding.secondCompanyScoreText.setText(searchResult.companies.get(0).plScore + getString(R.string.pt));
        } else {
            binding.secondCompanyProgressbar.setProgress(0);
            binding.secondCompanyScoreText.setText("?");
        }

        if (searchResult.altText != null) {
            binding.plDataLayout.setVisibility(View.GONE);
            binding.altText.setVisibility(View.VISIBLE);
            binding.altText.setText(searchResult.altText);
        } else {
            binding.altText.setVisibility(View.GONE);
            binding.plDataLayout.setVisibility(View.VISIBLE);

            if (searchResult.companies.get(0).description != null) {
                binding.description.setVisibility(View.VISIBLE);
                binding.description.setText(searchResult.companies.get(0).description);
            } else {
                binding.description.setVisibility(View.GONE);
            }
        }

        if (searchResult.askForSupport()) {
            binding.seePolaFriends.setVisibility(View.VISIBLE);
            binding.seePolaFriends.setOnClickListener((view) -> {
                if (delegate != null) {
                    delegate.onsSeePolaFriendsAction();
                }
            });
        } else {
            binding.seePolaFriends.setVisibility(View.GONE);
        }

        if (searchResult.companies.get(0).is_friend != null && searchResult.companies.get(0).is_friend && searchResult.friend_text != null) {
            binding.isFriendLayout.setVisibility(View.VISIBLE);
            binding.isFriendText.setText(searchResult.friend_text);
        }

        binding.productInfoCard.setOnClickListener(v -> eventBus.post(new ProductDetailsFragmentDismissedEvent()));
    }
}
