package pl.pola_app.ui.activity

import android.os.Bundle
import android.util.Log
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.squareup.otto.Bus
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.pola_app.TestApplication
import pl.pola_app.helpers.EventLogger
import pl.pola_app.helpers.SessionId
import pl.pola_app.model.SearchResult
import pl.pola_app.network.Api
import pl.pola_app.testutil.SearchUtil
import pl.pola_app.ui.adapter.ProductList
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import org.mockito.Mockito.verifyNoMoreInteractions

import org.mockito.Mockito.`when`


@Config(application = TestApplication::class)
@RunWith(
    RobolectricTestRunner::class
)
class MainPresenterTest {
    @Mock
    private lateinit var viewBinder: MainViewBinder
    @Mock
    private lateinit var productList: ProductList
    @Mock
    private lateinit var api: Api
    @Mock
    private lateinit var eventBus: Bus
    @Mock
    private lateinit var logger: EventLogger
    @Mock
    private lateinit var sessionId: SessionId
    @Mock
    private lateinit var mockCall: Call<SearchResult>
    @Mock
    private lateinit var presenter: MainPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = MainPresenter(viewBinder,productList,api,logger,sessionId,eventBus)

        Mockito.`when`<Call<SearchResult>>(
            api.getByCode(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(mockCall)
        Mockito.`when`<Call<SearchResult>>(
            api.getByCode(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(mockCall)
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterBus() {
        presenter.register()
        verify(eventBus).register(presenter)
    }

    @Test
    @Throws(Exception::class)
    fun testUnregisterBus() {
        presenter.unregister()
        verify(eventBus).unregister(presenter)
    }

//    @Test
//    @Throws(Exception::class)
//    fun testCallCanceledOnUnregister() {
//        presenter.onBarcode("code", true)
//        presenter.unregister()
//        verify(mockCall).cancel()
//    }


    @Test
    @Throws(Exception::class)
    fun testDontAddExistingProduct() {
        `when`(productList.itemExists("itemA")).thenReturn(true)
        presenter.onBarcode("itemA", true)
        verify(productList, never()).createProductPlaceholder()
        verifyNoMoreInteractions(api)
    }

    @Test
    @Throws(Exception::class)
    fun testAddProduct() {
        `when`(sessionId.get()).thenReturn("sessionId")
        `when`(viewBinder.deviceYear).thenReturn(2017)
        presenter.onBarcode("barcode", true)
        verify(productList).createProductPlaceholder()
        verify(api).getByCode("barcode", "sessionId")
    }

    @Test
    @Throws(Exception::class)
    fun testAddProductForOldDevices() {
        `when`(sessionId.get()).thenReturn("sessionId")
        `when`(viewBinder.deviceYear).thenReturn(2009)
        presenter.onBarcode("barcode", true)
        verify(productList).createProductPlaceholder()
        verify(api).getByCode("barcode", "sessionId", true)
    }

    @Test
    @Throws(Exception::class)
    fun testProductAddedOnResponse() {
        val searchResult = SearchUtil.createSearchResult(1)
        presenter.onResponse(null, Response.success(searchResult))
        verify(productList).addProduct(searchResult)
        verifyNoMoreInteractions(productList)
    }

    @Test
    @Throws(Exception::class)
    fun testResumeScanningOnResponse() {
        val searchResult = SearchUtil.createSearchResult(1)
        presenter.onResponse(null, Response.success(searchResult))
        verify(viewBinder).resumeScanning()
    }

    @Test
    @Throws(Exception::class)
    fun testResponseForUnsupportedBarCodes() {
        var searchResult = SearchUtil.createSearchResult(1)
        searchResult = searchResult.copy(product_id = null)
        try {
            presenter.onResponse(null, Response.success(searchResult))
        } catch (e: Exception) {
            fail(Log.getStackTraceString(e))
        }
    }


    @Test
    @Throws(Exception::class)
    fun testPlaceholderRemovedOnFailedRequest() {
        presenter.onFailure(null, Throwable("msg"))
        verify(productList).removeProductPlaceholder()
        verifyNoMoreInteractions(productList)
    }

    @Test
    @Throws(Exception::class)
    fun testResumeScanningOnFailure() {
        presenter.onFailure(null, Throwable("msg"))
        verify(viewBinder).resumeScanning()
    }

    @Test
    @Throws(Exception::class)
    fun testShowErrorMessageOnFailure() {
        presenter.onFailure(null, Throwable("msg"))
        verify(viewBinder).showErrorMessage("msg")
    }

    @Test
    @Throws(Exception::class)
    fun testShowNoConnectionMessage() {
        val noConnectionMessage =
            "Unable to resolve host \"www.pola-app.pl\": No address associated with hostname"
        presenter.onFailure(null, Throwable(noConnectionMessage))
        verify(viewBinder).showNoConnectionMessage()
    }

    @Test
    @Throws(Exception::class)
    fun testOpenProductDetailsOnClick() {
        val searchResult = SearchUtil.createSearchResult(1)
        presenter.onItemClicked(searchResult)
        verify(viewBinder).openProductDetails(searchResult)
        verify(viewBinder).turnOffTorch()
    }

    @Test
    @Throws(Exception::class)
    fun testDismissDetailsView() {
        presenter.productDetailsFragmentDismissed(null)
        verify(viewBinder).dismissProductDetailsView()
    }

    @Test
    @Throws(Exception::class)
    fun testSaveState() {
        val bundle = Bundle()
        presenter.onSaveState(bundle)
        verify(productList).writeToBundle(bundle)
    }
}