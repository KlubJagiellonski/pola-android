package pl.pola_app.feature.main

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import pl.pola_app.feature.base.BaseViewModel
import pl.pola_app.repository.PolaApi
import javax.inject.Inject

class MainViewModel @Inject constructor(
    var polaApi: PolaApi
) : BaseViewModel() {

    var torchOn = MutableLiveData<Boolean>(false)

    var barcodeList = MutableLiveData<List<String>>(listOf())

    @SuppressLint("CheckResult")
    fun getRating(barcode: String) {

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