/**
 * Suggest that we should prepare business packages
 */
package pl.pola_app.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.model.SearchResult;
import pl.pola_app.presenter.VideoMessagePresenter;
import pl.pola_app.presenter.view.VideoMessageView;
import pl.pola_app.ui.delegate.HelpMessageFragmentDelegate;
import pl.pola_app.ui.fragment.HelpMessageFragment;
import pl.pola_app.ui.fragment.VideoCaptureFragment;
import pl.tajchert.nammu.Nammu;

public class VideoMessageActivity extends AppCompatActivity implements VideoMessageView, HelpMessageFragmentDelegate {

    private static final String SEARCH_RESULT = "SEARCH_RESULT";
    private static final String DEVICE_ID = "DEVICE_ID";
    @Inject
    VideoMessagePresenter presenter;
;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        PolaApplication.component(this).inject(this);
        presenter.initView(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        SearchResult searchResult = Parcels.unwrap(getIntent().getParcelableExtra(SEARCH_RESULT));
        String deviceId = getIntent().getStringExtra(DEVICE_ID);
        presenter.onCreate(searchResult, deviceId);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void displayHelpMessageScreen(SearchResult searchResult) {
        getSupportFragmentManager().beginTransaction().replace(R.id.video_message_container, HelpMessageFragment.newInstance(searchResult)).commit();
    }

    @Override
    public void displayHelpScreen(SearchResult searchResult, String deviceId) {
        getSupportFragmentManager().beginTransaction().replace(R.id.video_message_container, VideoCaptureFragment.newInstance(searchResult, deviceId)).commit();
    }

    @Override
    public void hideView() {
        finish();
    }

    @Override
    public void onWantToHelpButtonClick(boolean doNotShowNextTime) {
        presenter.onWantToHelpButtonClick(doNotShowNextTime);
    }

    @Override
    public void onNextTimeButtonClick(boolean doNotShowNextTime) {
        presenter.onNextTimeButtonClick(doNotShowNextTime);
    }

    public static final class IntentFactory {
        public static Intent forStart(Context context, SearchResult searchResult, String deviceId) {
            final Intent result = new Intent(context, VideoMessageActivity.class);
            result.putExtra(SEARCH_RESULT, Parcels.wrap(searchResult));
            result.putExtra(DEVICE_ID, deviceId);
            return result;
        }
    }
}
