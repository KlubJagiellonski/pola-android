package pl.pola_app.feature.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.SingleObserver
import pl.pola_app.feature.base.BaseViewModel
import pl.pola_app.repository.PolaApi
import pl.pola_app.repository.SessionId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.pola_app.repository.SearchResult
import javax.inject.Inject

class MainViewModel @Inject constructor(
    var polaApi: PolaApi,
    var sessionId: SessionId
) : BaseViewModel() {

    var torchOn = MutableLiveData<Boolean>(false)

    var barcodeList = MutableLiveData<List<String>>(listOf())
    var searchResultList = MutableLiveData<List<SearchResult>>(listOf())

    @SuppressLint("CheckResult")
    fun getRating(barcode: String) {
        polaApi.getByCode(barcode, sessionId.get(),true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<SearchResult> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onSuccess(searchResult: SearchResult) {
                    val listTmp = mutableListOf<SearchResult>()
                    searchResultList.value?.let { listTmp.addAll(it) }
                    listTmp.add(searchResult)
                    searchResultList.value = listTmp
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun addNewBarcodeIfNotExits(barcode: String) {
        val listTmp = mutableListOf<String>()
        listTmp.addAll(barcodeList.value ?: listOf())

        if (!listTmp.contains(barcode)) {
            listTmp.add(barcode)
            barcodeList.value = listTmp
            getRating(barcode)
        }
    }
}