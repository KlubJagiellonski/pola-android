package pl.pola_app.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import pl.pola_app.TestApplication;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.model.SearchResult;
import pl.pola_app.network.Api;
import pl.pola_app.testutil.SearchUtil;
import pl.pola_app.ui.adapter.ProductList;
import pl.pola_app.ui.event.ReportButtonClickedEvent;
import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Config(application = TestApplication.class)
@RunWith(RobolectricGradleTestRunner.class)
public class MainPresenterTest {

    @Mock private MainViewBinder viewBinder;
    @Mock private ProductList productList;
    @Mock private Api api;
    @Mock private Bus eventBus;
    @Mock private EventLogger logger;
    @Mock private SessionId sessionId;
    @Mock private Call<SearchResult> mockCall;
    private MainPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new MainPresenter(viewBinder, productList, api, logger, sessionId, eventBus);
        when(api.getByCode(anyString(), anyString())).thenReturn(mockCall);
        when(api.getByCode(anyString(), anyString(), anyBoolean())).thenReturn(mockCall);
    }

    @Test
    public void testRegisterBus() throws Exception {
        presenter.register();

        verify(eventBus).register(presenter);
    }

    @Test
    public void testUnregisterBus() throws Exception {
        presenter.unregister();

        verify(eventBus).unregister(presenter);
    }

    @Test
    public void testCallCanceledOnUnregister() throws Exception {
        presenter.onBarcode("code", true);
        presenter.unregister();

        verify(mockCall).cancel();
    }

    @Test
    public void testDontAddExistingProduct() throws Exception {
        when(productList.itemExists("itemA")).thenReturn(true);
        presenter.onBarcode("itemA", true);

        verify(productList, never()).createProductPlaceholder();
        verifyNoMoreInteractions(api);
    }

    @Test
    public void testAddProduct() throws Exception {
        when(sessionId.get()).thenReturn("sessionId");
        when(viewBinder.getDeviceYear()).thenReturn(2017);
        presenter.onBarcode("barcode", true);

        verify(productList).createProductPlaceholder();
        verify(api).getByCode("barcode", "sessionId");
    }

    @Test
    public void testAddProductForOldDevices() throws Exception {
        when(sessionId.get()).thenReturn("sessionId");
        when(viewBinder.getDeviceYear()).thenReturn(2009);
        presenter.onBarcode("barcode", true);

        verify(productList).createProductPlaceholder();
        verify(api).getByCode("barcode", "sessionId", true);
    }

    @Test
    public void testProductAddedOnResponse() throws Exception {
        final SearchResult searchResult = SearchUtil.createSearchResult(1);

        presenter.onResponse(null, Response.success(searchResult));

        verify(productList).addProduct(searchResult);
        verifyNoMoreInteractions(productList);
    }

    @Test
    public void testResumeScanningOnResponse() throws Exception {
        final SearchResult searchResult = SearchUtil.createSearchResult(1);
        presenter.onResponse(null, Response.success(searchResult));

        verify(viewBinder).resumeScanning();
    }

    @Test
    public void testResponseForUnsupportedBarCodes() throws Exception {
        final SearchResult searchResult = SearchUtil.createSearchResult(1);
        searchResult.product_id = null;

        try {
            presenter.onResponse(null, Response.success(searchResult));
        } catch (Exception e) {
            fail(Log.getStackTraceString(e));
        }
    }

    @Test
    public void testPlaceholderRemovedOnFailedRequest() throws Exception {
        presenter.onFailure(null, new Throwable("msg"));

        verify(productList).removeProductPlaceholder();
        verifyNoMoreInteractions(productList);
    }

    @Test
    public void testResumeScanningOnFailure() throws Exception {
        presenter.onFailure(null, new Throwable("msg"));

        verify(viewBinder).resumeScanning();
    }

    @Test
    public void testShowErrorMessageOnFailure() throws Exception {
        presenter.onFailure(null, new Throwable("msg"));

        verify(viewBinder).showErrorMessage("msg");
    }

    @Test
    public void testShowNoConnectionMessage() throws Exception {
        final String noConnectionMessage = "Unable to resolve host \"www.pola-app.pl\": No address associated with hostname";
        presenter.onFailure(null, new Throwable(noConnectionMessage));

        verify(viewBinder).showNoConnectionMessage();
    }

    @Test
    public void testOpenProductDetailsOnClick() throws Exception {
        final SearchResult searchResult = SearchUtil.createSearchResult(1);
        presenter.onItemClicked(searchResult);

        verify(viewBinder).openProductDetails(searchResult);
        verify(viewBinder).turnOffTorch();
    }

    @Test
    public void testDismissDetailsView() throws Exception {
        presenter.productDetailsFragmentDismissed(null);

        verify(viewBinder).dismissProductDetailsView();
    }

    @Test
    public void testLaunchReportActivity() throws Exception {
        final SearchResult searchResult = SearchUtil.createSearchResult(1);
        searchResult.product_id = 123;
        presenter.reportButtonClicked(new ReportButtonClickedEvent(searchResult));

        verify(viewBinder).launchReportActivity("123");
    }

    @Test
    public void testSaveState() throws Exception {
        Bundle bundle = new Bundle();
        presenter.onSaveState(bundle);

        verify(productList).writeToBundle(bundle);
    }
}
