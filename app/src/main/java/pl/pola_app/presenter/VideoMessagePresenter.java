package pl.pola_app.presenter;


import javax.inject.Inject;

import pl.pola_app.helpers.SettingsPreference;
import pl.pola_app.model.SearchResult;
import pl.pola_app.presenter.view.VideoMessageView;

//TODO it should be activity scope but for this moment scope is not supported.
public class VideoMessagePresenter {

    private final SettingsPreference settingsPreference;
    private VideoMessageView videoMessageView;
    private SearchResult searchResult;
    private String deviceId;

    @Inject
    VideoMessagePresenter(SettingsPreference settingsPreference) {
        this.settingsPreference = settingsPreference;
    }

    public void initView(VideoMessageView videoMessageView) {
        this.videoMessageView = videoMessageView;
    }

    public void onDestroy() {
        videoMessageView = null;
    }

    public void onCreate(SearchResult searchResult, String deviceId) {
        this.searchResult = searchResult;
        this.deviceId = deviceId;
        if (videoMessageView == null) {
            return;
        }
        if (settingsPreference.shouldDisplayHelpMessageScreen()) {
            videoMessageView.displayHelpMessageScreen(searchResult);
            return;
        }
        videoMessageView.displayHelpScreen(searchResult, deviceId);
    }

    public void onWantToHelpButtonClick(boolean doNotShowNextTime) {
        if (doNotShowNextTime) {
            settingsPreference.neverDisplayHelpMessageScreen();
        }
        if (videoMessageView != null) {
            videoMessageView.displayHelpScreen(searchResult, deviceId);
        }
    }

    public void onNextTimeButtonClick(boolean doNotShowNextTime) {
        if (doNotShowNextTime) {
            settingsPreference.neverDisplayHelpMessageScreen();
        }
        if (videoMessageView != null) {
            videoMessageView.hideView();
        }
    }
}
