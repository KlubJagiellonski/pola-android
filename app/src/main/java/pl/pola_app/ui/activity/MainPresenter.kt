package pl.pola_app.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import pl.pola_app.PolaApplication
import pl.pola_app.helpers.EventLogger
import pl.pola_app.helpers.SessionId
import pl.pola_app.model.SearchResult
import pl.pola_app.network.Api
import pl.pola_app.ui.adapter.ProductList
import pl.pola_app.ui.adapter.ProductsAdapter
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent
import pl.pola_app.ui.fragment.BarcodeListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class MainPresenter(
    private val viewBinder: MainViewBinder,
    private val productList: ProductList,
    private val api: Api,
    private val logger: EventLogger,
    private val sessionId: SessionId,
    private val eventBus: Bus
) : Callback<SearchResult?>, BarcodeListener {
    private val handlerScanner = Handler()
    private var reportResultCall: Call<SearchResult>? = null
    private val runnableResumeScan = Runnable { viewBinder.resumeScanning() }
    private var currentSearchResult: SearchResult? = null
    fun register() {
        eventBus.register(this)
    }

    fun unregister() {
        eventBus.unregister(this)
        reportResultCall?.cancel()
    }

    override fun onBarcode(barcode: String, fromCamera: Boolean) {
        logger.logSearch(barcode, sessionId.get(), if (fromCamera) "camera" else "keyboard")
        if (productList.itemExists(barcode)) {
            handlerScanner.removeCallbacks(runnableResumeScan)
            handlerScanner.postDelayed(runnableResumeScan, millisecondsBetweenExisting.toLong())
        } else {
            productList.createProductPlaceholder()
            reportResultCall = if (viewBinder.deviceYear < 2010) api.getByCode(
                barcode,
                sessionId.get(),
                true
            ) else api.getByCode(barcode, sessionId.get())
            reportResultCall?.enqueue(this)
        }
    }

    override fun onResponse(call: Call<SearchResult?>, response: Response<SearchResult?>) {
        val searchResult = response.body() ?: return
        currentSearchResult = searchResult
        searchResult.code
        logger.logContentView(
            searchResult.name + "",
            "company_received", searchResult.product_id.toString(),
            searchResult.code,
            sessionId.get()
        )
        productList.addProduct(searchResult)
        viewBinder.resumeScanning()
        viewBinder.setSupportPolaAppButtonVisibility(searchResult.askForSupport(), searchResult)
    }

    override fun onFailure(call: Call<SearchResult?>, t: Throwable) {
        if ("Unable to resolve host \"www.pola-app.pl\": No address associated with hostname" == t.localizedMessage) { //TODO this is awefull
            viewBinder.showNoConnectionMessage()
        } else {
            viewBinder.showErrorMessage(t.localizedMessage)
        }
        handlerScanner.removeCallbacks(runnableResumeScan)
        productList.removeProductPlaceholder()
        viewBinder.resumeScanning()
    }

    fun onItemClicked(searchResult: SearchResult) {
        logger.logContentView(
            searchResult.name + "",
            "card_opened", searchResult.product_id.toString(),
            searchResult.code,
            sessionId.get()
        )
        viewBinder.turnOffTorch()
        viewBinder.openProductDetails(searchResult)
    }

    fun onSaveState(bundle: Bundle) {
        productList.writeToBundle(bundle)
    }

    @Subscribe
    fun productDetailsFragmentDismissed(event: ProductDetailsFragmentDismissedEvent?) {
        viewBinder.dismissProductDetailsView()
    }

    fun setCurrentSearchResult(currentSearchResult: SearchResult?) {
        this.currentSearchResult = currentSearchResult
    }

    fun onBackStackChange(isNotBackStackEmpty: Boolean) {
        viewBinder.setSupportPolaAppButtonVisibility(
            !isNotBackStackEmpty && currentSearchResult != null && currentSearchResult?.askForSupport() == true,
            currentSearchResult
        )
    }

    fun onSupportPolaButtonClick() {
        if (currentSearchResult != null) {
            viewBinder.openWww(currentSearchResult, currentSearchResult?.donate?.url)
        }
    }

    fun onSupportPolaFinished() {
        currentSearchResult?.run {
            currentSearchResult = this.copy(donate = null)
            viewBinder.setSupportPolaAppButtonVisibility(false, currentSearchResult)
        }
    }

    companion object {
        private const val millisecondsBetweenExisting =
            2000 //otherwise it will scan and vibrate few times a second

        fun create(
            applicationContext: Context?, viewBinder: MainViewBinder,
            productList: ProductList,
            productsAdapter: ProductsAdapter,
            sessionId: SessionId,
            eventBus: Bus
        ): MainPresenter {
            productList.setOnProductListChanged { productsAdapter.notifyDataSetChanged() }
            viewBinder.setAdapter(productsAdapter)
            val api: Api = PolaApplication.Companion.retrofit.create<Api>(
                Api::class.java
            )
            val logger = EventLogger(applicationContext!!)
            val mainPresenter =
                MainPresenter(viewBinder, productList, api, logger, sessionId, eventBus)
            productsAdapter.setOnProductClickListener { searchResult ->
                mainPresenter.onItemClicked(
                    searchResult
                )
            }
            return mainPresenter
        }
    }
}