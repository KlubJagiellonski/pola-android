package pl.pola_app.feature.details

import androidx.lifecycle.MutableLiveData
import pl.pola_app.feature.base.BaseViewModel
import pl.pola_app.repository.SearchResult
import javax.inject.Inject

class DetailsViewModel @Inject constructor() : BaseViewModel() {

    var searchResult = MutableLiveData<SearchResult>()

}