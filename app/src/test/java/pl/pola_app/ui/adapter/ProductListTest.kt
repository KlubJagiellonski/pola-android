package pl.pola_app.ui.adapter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.pola_app.TestApplication
import pl.pola_app.model.SearchResult
import pl.pola_app.testutil.SearchUtil
import java.lang.Exception

@Config(application = TestApplication::class)
@RunWith(
    RobolectricTestRunner::class
)
class ProductListTest {
    private lateinit var productList: ProductList
    @Before
    @Throws(Exception::class)
    fun setUp() {
        productList = ProductList.create(null)
    }

    @Test
    @Throws(Exception::class)
    fun testCreate() {
        productList.addProduct(SearchUtil.createSearchResult(1))
        productList.addProduct(SearchUtil.createSearchResult(2))
        productList.addProduct(SearchUtil.createSearchResult(3))
        val bundle = Bundle()
        productList.writeToBundle(bundle)
        val productListNew = ProductList.create(bundle)
        Assert.assertEquals(productList[0], productListNew[0])
        Assert.assertEquals(productList[1], productListNew[1])
        Assert.assertEquals(productList[2], productListNew[2])
    }

    @Test
    @Throws(Exception::class)
    fun testSize() {
        Assert.assertEquals(0, productList.size().toLong())
        productList.addProduct(SearchUtil.createSearchResult(1))
        Assert.assertEquals(1, productList.size().toLong())
        productList.addProduct(SearchUtil.createSearchResult(2))
        Assert.assertEquals(2, productList.size().toLong())
        productList.addProduct(SearchUtil.createSearchResult(3))
        productList.addProduct(SearchUtil.createSearchResult(4))
        productList.addProduct(SearchUtil.createSearchResult(5))
        productList.addProduct(SearchUtil.createSearchResult(6))
        productList.addProduct(SearchUtil.createSearchResult(6))
        Assert.assertEquals(7, productList.size().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun testProductAdded() {
        val searchResult: SearchResult = SearchUtil.createSearchResult(1)
        productList.addProduct(searchResult)
        Assert.assertEquals(searchResult, productList[0])
    }

    @Test
    @Throws(Exception::class)
    fun testProductAddedToFrontOfList() {
        val searchResultA: SearchResult = SearchUtil.createSearchResult(1)
        val searchResultB: SearchResult = SearchUtil.createSearchResult(2)
        productList.addProduct(searchResultA)
        productList.addProduct(searchResultB)
        Assert.assertEquals(2, productList.size().toLong())
        Assert.assertEquals(searchResultA, productList[1])
        Assert.assertEquals(searchResultB, productList[0])
        Assert.assertNotEquals(searchResultA, productList[0])
        Assert.assertNotEquals(searchResultB, productList[1])
    }

    @Test
    @Throws(Exception::class)
    fun testNotifyChangedOnAdd() {
        val onProductListChanged: OnProductListChanged = Mockito.mock<OnProductListChanged>(
            OnProductListChanged::class.java
        )
        productList.setOnProductListChanged { onProductListChanged.onChanged() }
        productList.addProduct(SearchUtil.createSearchResult(1))
        verify<OnProductListChanged>(onProductListChanged).onChanged()
    }

    @Test
    @Throws(Exception::class)
    fun testItemDoesNotExistForEmptyList() {
        Assert.assertFalse(productList.itemExists("code"))
    }

    @Test
    @Throws(Exception::class)
    fun testItemDoesNotExist() {
        productList.addProduct(SearchUtil.createSearchResult(1))
        Assert.assertFalse(productList.itemExists("code"))
    }

    @Test
    @Throws(Exception::class)
    fun testItemExists() {
        val searchResult: SearchResult = SearchUtil.createSearchResult(1)
        val code = searchResult.code
        productList.addProduct(searchResult)
        Assert.assertTrue(productList.itemExists(code))
    }
}