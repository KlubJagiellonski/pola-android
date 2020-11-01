package pl.pola_app.feature.browser

import androidx.lifecycle.MutableLiveData
import pl.pola_app.feature.base.BaseViewModel
import javax.inject.Inject

class BrowserViewModel  @Inject constructor() : BaseViewModel() {

    var url = MutableLiveData<String>()
}