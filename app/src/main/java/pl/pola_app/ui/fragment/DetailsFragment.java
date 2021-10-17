package pl.pola_app.ui.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Bus;

import org.parceler.Parcels;

import javax.inject.Inject;

import pl.pola_app.R;
import pl.pola_app.databinding.FragmentProductDetailsBinding;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.activity.ActivityWebView;
import pl.pola_app.ui.delegate.DetailsFragmentDelegate;

public abstract class DetailsFragment extends DialogFragment {

    private FragmentProductDetailsBinding binding;

    @Inject
    Bus eventBus;

    @Inject
    Resources resources;

    protected SearchResult searchResult;

    protected DetailsFragmentDelegate delegate;

    protected EventLogger logger;
    protected SessionId sessionId;

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

    void onFriendsClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("item", getString(R.string.polas_friends));
        bundle.putString("device_id", sessionId.get());
        logger.logCustom("product_details_friend", bundle);

        Intent intent = new Intent(getActivity(), ActivityWebView.class);
        intent.putExtra("url", Utils.URL_POLA_FRIENDS);
        startActivity(intent);
    }
}
