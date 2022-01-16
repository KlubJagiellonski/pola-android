package pl.pola_app.ui.activity

import androidx.recyclerview.widget.RecyclerView
import pl.pola_app.model.SearchResult

internal interface MainViewBinder {
    fun setAdapter(adapter: RecyclerView.Adapter<*>)
    fun resumeScanning()
    fun turnOffTorch()
    fun openProductDetails(searchResult: SearchResult)
    fun showNoConnectionMessage()
    fun showErrorMessage(message: String?)
    fun dismissProductDetailsView()
    fun openWww(searchResult: SearchResult?, url: String?)
    fun setSupportPolaAppButtonVisibility(isVisible: Boolean, searchResult: SearchResult?)
    val deviceYear: Int
}