package pl.pola_app.ui.adapter

import android.os.Bundle
import org.parceler.Parcels
import pl.pola_app.model.SearchResult
import java.util.*

class ProductList private constructor(private val searchResults: MutableList<SearchResult?>) {
    private var onProductListChanged: OnProductListChanged? = null
    fun setOnProductListChanged(onProductListChanged: () -> Unit) {
        this.onProductListChanged = object : OnProductListChanged{
            override fun onChanged() {
                onProductListChanged.invoke()
            }

        }
    }

    fun writeToBundle(bundle: Bundle) {
        bundle.putParcelable(
            SearchResult::class.java.name, Parcels.wrap<List<SearchResult?>>(
                searchResults
            )
        )
    }

    fun addProduct(searchResult: SearchResult?) {
        if (searchResults.size > 0 && searchResults[0] == null) {
            searchResults[0] = searchResult
        } else {
            searchResults.add(0, searchResult)
        }
        notifyOnChanged()
    }

    fun createProductPlaceholder() {
        searchResults.add(0, null)
        notifyOnChanged()
    }

    fun removeProductPlaceholder() {
        if (searchResults.size > 0 && searchResults[0] == null) {
            searchResults.removeAt(0)
            notifyOnChanged()
        }
    }

    fun itemExists(code: String): Boolean {
        for (p in searchResults) {
            if (p != null) {
                if (p.code == code) {
                    searchResults.remove(p)
                    searchResults.add(0, p)
                    notifyOnChanged()
                    return true
                }
            }
        }
        return false
    }

    fun size(): Int {
        return searchResults.size
    }

    operator fun get(position: Int): SearchResult? {
        return searchResults[position]
    }

    private fun notifyOnChanged() {
        onProductListChanged?.onChanged()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun create(bundle: Bundle?): ProductList {
            val searchResults: MutableList<SearchResult?> = if (bundle != null) {
                Parcels.unwrap(
                    bundle.getParcelable(
                        SearchResult::class.java.name
                    )
                )
            } else {
                ArrayList()
            }
            return ProductList(searchResults)
        }
    }
}