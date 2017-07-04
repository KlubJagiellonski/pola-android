package pl.pola_app.presenter.view;


import pl.pola_app.model.SearchResult;

public interface VideoMessageView {
    void displayHelpMessageScreen(SearchResult searchResult);
    void displayHelpScreen(SearchResult searchResult, String deviceId);
    void hideView();
}
