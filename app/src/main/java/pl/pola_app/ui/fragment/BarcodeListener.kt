package pl.pola_app.ui.fragment

interface BarcodeListener {
    fun onBarcode(barcode: String, fromCamera: Boolean)
}