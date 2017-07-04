package pl.pola_app.ui.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.VideoView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.R;
import pl.pola_app.model.SearchResult;
import pl.pola_app.ui.delegate.HelpMessageFragmentDelegate;

public class HelpMessageFragment extends Fragment {

    private static final String SEARCH_RESULT = "SEARCH_RESULT";
    private HelpMessageFragmentDelegate delegate;


    @Bind(R.id.do_not_show_this_screen_again)
    CheckBox doNotShowAgain;

    @Bind(R.id.ask_fro_pics_title)
    TextView title;

    @Bind(R.id.ask_fro_pics_text)
    TextView messageText;

    @Bind(R.id.make_movie_button)
    TextView makeMovieButton;

    @Bind(R.id.sample_video_view)
    VideoView videoView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HelpMessageFragmentDelegate) {
            delegate = (HelpMessageFragmentDelegate) context;
        }
    }

    public static HelpMessageFragment newInstance(SearchResult searchResult) {
        final HelpMessageFragment result = new HelpMessageFragment();
        final Bundle args = new Bundle();
        args.putParcelable(SEARCH_RESULT, Parcels.wrap(searchResult));
        result.setArguments(args);
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prepareVideoView();
        final Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalArgumentException("Please pass SearchResult as fragment args");
        }
        SearchResult searchResult = Parcels.unwrap(arguments.getParcelable(SEARCH_RESULT));
        title.setText(searchResult.askForPicsTitle());
        messageText.setText(searchResult.askForPicsText());
        makeMovieButton.setText(searchResult.askForPicsButtonStart());
    }

    private void prepareVideoView() {
        String path = "android.resource://" + getContext().getPackageName() + "/" + R.raw.video_file;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f,0f);
        });
    }

    @Override
    public void onDestroy() {
        delegate = null;
        ButterKnife.unbind(this);
        super.onDestroy();
    }


    @OnClick(R.id.make_movie_button)
    void onWantToHelpButtonClick() {
        if (delegate != null) {
            delegate.onWantToHelpButtonClick(doNotShowAgain.isChecked());
        }
    }
}
