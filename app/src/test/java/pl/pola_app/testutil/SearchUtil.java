package pl.pola_app.testutil;

import pl.pola_app.model.SearchResult;

public class SearchUtil {

    public static SearchResult createSearchResult(int id) {
        SearchResult searchResult = new SearchResult();
        searchResult.code = "code" + id;
        searchResult.name = "name" + id;
        searchResult.card_type = "card_type" + id;
        searchResult.altText = "altText" + id;
        searchResult.description = "description" + id;
        searchResult.report_text = "report_text" + id;
        searchResult.report_button_text = "report_button_text" + id;
        searchResult.report_button_type = "report_button_type" + id;
        searchResult.product_id = id;
        searchResult.plScore = 1 + id;
        searchResult.plCapital = 2 + id;
        searchResult.plWorkers = 3 + id;
        searchResult.plRnD = 4 + id;
        searchResult.plRegistered = 5 + id;
        searchResult.plNotGlobEnt = 6 + id;

        return searchResult;
    }
}
