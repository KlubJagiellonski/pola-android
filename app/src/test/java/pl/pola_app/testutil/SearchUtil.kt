package pl.pola_app.testutil

import pl.pola_app.model.SearchResult

object SearchUtil {
    fun createSearchResult(id: Int): SearchResult {
        return SearchResult(
            id,
            "code$id",
            "name$id",
            "card_type$id",
            1 + id,
            "altText$id",
            2 + id,
            3 + id,
            4 + id,
            5 + id,
            6 + id,
            null,
            "description$id",
            "report_text$id",
            "report_button_text$id",
            "report_button_type$id",
            null,
            null
        )
    }
}