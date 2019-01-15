package pl.pola_app.ui.adapter;

import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import pl.pola_app.TestApplication;
import pl.pola_app.model.SearchResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.pola_app.testutil.SearchUtil.createSearchResult;

@Config(application = TestApplication.class)
@RunWith(RobolectricTestRunner.class)
public class ProductListTest {
    private ProductList productList;

    @org.junit.Before
    public void setUp() throws Exception {
        productList = ProductList.create(null);
    }

    @Test
    public void testCreate() throws Exception {
        productList.addProduct(createSearchResult(1));
        productList.addProduct(createSearchResult(2));
        productList.addProduct(createSearchResult(3));

        final Bundle bundle = new Bundle();
        productList.writeToBundle(bundle);

        final ProductList productListNew = ProductList.create(bundle);

        assertEquals(this.productList.get(0), productListNew.get(0));
        assertEquals(this.productList.get(1), productListNew.get(1));
        assertEquals(this.productList.get(2), productListNew.get(2));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, productList.size());
        productList.addProduct(createSearchResult(1));
        assertEquals(1, productList.size());
        productList.addProduct(createSearchResult(2));
        assertEquals(2, productList.size());
        productList.addProduct(createSearchResult(3));
        productList.addProduct(createSearchResult(4));
        productList.addProduct(createSearchResult(5));
        productList.addProduct(createSearchResult(6));
        productList.addProduct(createSearchResult(6));
        assertEquals(7, productList.size());
    }

    @Test
    public void testProductAdded() throws Exception {
        SearchResult searchResult = createSearchResult(1);
        productList.addProduct(searchResult);

        assertEquals(searchResult, productList.get(0));
    }

    @Test
    public void testProductAddedToFrontOfList() throws Exception {
        final SearchResult searchResultA = createSearchResult(1);
        final SearchResult searchResultB = createSearchResult(2);

        productList.addProduct(searchResultA);
        productList.addProduct(searchResultB);

        assertEquals(2, productList.size());
        assertEquals(searchResultA, productList.get(1));
        assertEquals(searchResultB, productList.get(0));
        assertNotEquals(searchResultA, productList.get(0));
        assertNotEquals(searchResultB, productList.get(1));
    }

    @Test
    public void testNotifyChangedOnAdd() throws Exception {
        final OnProductListChanged onProductListChanged = mock(OnProductListChanged.class);
        productList.setOnProductListChanged(onProductListChanged);
        productList.addProduct(createSearchResult(1));

        verify(onProductListChanged).onChanged();
    }

    @Test
    public void testItemDoesNotExistForEmptyList() throws Exception {
        assertFalse(productList.itemExists("code"));
    }

    @Test
    public void testItemDoesNotExist() throws Exception {
        productList.addProduct(createSearchResult(1));

        assertFalse(productList.itemExists("code"));
    }

    @Test
    public void testItemExists() throws Exception {
        final SearchResult searchResult = createSearchResult(1);
        final String code = searchResult.code;
        productList.addProduct(searchResult);

        assertTrue(productList.itemExists(code));
    }

}
