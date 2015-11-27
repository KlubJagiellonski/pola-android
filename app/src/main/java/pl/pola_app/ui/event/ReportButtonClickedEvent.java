package pl.pola_app.ui.event;

import pl.pola_app.model.SearchResult;

public class ReportButtonClickedEvent {
    public SearchResult searchResult;

    public ReportButtonClickedEvent(SearchResult searchResult) {
        this.searchResult = searchResult;
    }
}
