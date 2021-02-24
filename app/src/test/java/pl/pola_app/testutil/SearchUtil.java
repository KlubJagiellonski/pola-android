package pl.pola_app.testutil;

import pl.pola_app.model.SearchResult;

public class SearchUtil {

    public static SearchResult createSearchResult(int id) {
        SearchResult searchResult = new SearchResult();
        searchResult.code = "code" + id;
        searchResult.name = searchResult.name != null ? searchResult.name + id : searchResult.companies.get(0).name + id;
        searchResult.card_type = "card_type" + id;
        searchResult.altText = "altText" + id;
        searchResult.companies.get(0).description = "description" + id;
        searchResult.report.text = "report_text" + id;
        searchResult.report.button_text = "report_button_text" + id;
        searchResult.report.button_type = "report_button_type" + id;
        searchResult.product_id = id;
        searchResult.companies.get(0).plScore = 1 + id;
        searchResult.companies.get(0).plCapital = 2 + id;
        searchResult.companies.get(0).plWorkers = 3 + id;
        searchResult.companies.get(0).plRnD = 4 + id;
        searchResult.companies.get(0).plRegistered = 5 + id;
        searchResult.companies.get(0).plNotGlobEnt = 6 + id;

        return searchResult;
    }
}
