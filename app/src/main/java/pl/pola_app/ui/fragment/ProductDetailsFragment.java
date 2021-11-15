package pl.pola_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.databinding.FragmentProductDetailsBinding;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent;

public class ProductDetailsFragment extends DetailsFragment {

    private FragmentProductDetailsBinding binding;

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
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        PolaApplication.component(getActivity()).inject(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.companyName.setText(searchResult.name != null ? searchResult.name : searchResult.companies.get(0).name);

        if (searchResult.companies != null && searchResult.companies.get(0).plScore != null) {
            binding.plScoreBar.setProgress(searchResult.companies.get(0).plScore);
            binding.plScoreText.setText(searchResult.companies.get(0).plScore + getString(R.string.pt));
        } else {
            binding.plScoreBar.setProgress(0);
            binding.plScoreText.setText("?");
        }

        if (searchResult.companies != null && searchResult.companies.get(0).plCapital != null) {
            binding.plCapitalBar.setProgress(searchResult.companies.get(0).plCapital);
            binding.plCapitalScoreText.setText(searchResult.companies.get(0).plCapital + "%");
        } else {
            binding.plCapitalBar.setProgress(0);
            binding.plCapitalScoreText.setText("?");
        }

        if (searchResult.altText != null) {
            binding.plDataLayout.setVisibility(View.GONE);
            binding.altText.setVisibility(View.VISIBLE);
            binding.altText.setText(searchResult.altText);
        } else {
            binding.altText.setVisibility(View.GONE);
            binding.plDataLayout.setVisibility(View.VISIBLE);

            if (searchResult.companies.get(0).plWorkers != null && searchResult.companies.get(0).plWorkers != 0) {
                binding.buttonWorkers.setSelected(true);
            } else if (searchResult.companies.get(0).plWorkers == null) {
                binding.buttonWorkers.setEnabled(false);
            }

            if (searchResult.companies.get(0).plRnD != null && searchResult.companies.get(0).plRnD != 0) {
                binding.buttonRnd.setSelected(true);
            } else if (searchResult.companies.get(0).plRnD == null) {
                binding.buttonRnd.setEnabled(false);
            }

            if (searchResult.companies.get(0).plRegistered != null && searchResult.companies.get(0).plRegistered != 0) {
                binding.buttonRegistered.setSelected(true);
            } else if (searchResult.companies.get(0).plRegistered == null) {
                binding.buttonRegistered.setEnabled(false);
            }

            if (searchResult.companies.get(0).plNotGlobEnt != null && searchResult.companies.get(0).plNotGlobEnt != 0) {
                binding.buttonGlobent.setSelected(true);
            } else if (searchResult.companies.get(0).plNotGlobEnt == null) {
                binding.buttonGlobent.setEnabled(false);
            }

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

        if (searchResult.companies != null &&
                searchResult.companies.get(0).is_friend != null &&
                searchResult.companies.get(0).is_friend &&
                searchResult.friend_text != null) {
            binding.isFriendLayout.setVisibility(View.VISIBLE);
            binding.isFriendText.setText(searchResult.friend_text);
        }

        binding.productInfoCard.setOnClickListener(v -> eventBus.post(new ProductDetailsFragmentDismissedEvent()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        delegate = null;
    }
}
