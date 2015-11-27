package pl.pola_app.ui.event;

import pl.pola_app.model.SearchResult;

/**
 * Created by grzegorzkapusta on 08.10.2015.
 */
public class ProductItemClickedEvent {
    public SearchResult searchResult;

    public ProductItemClickedEvent(SearchResult item) {
        this.searchResult = item;
    }

}
