package pl.pola_app.ui.event

interface FlashActionListener {
    fun onFlashAction()
    val isTorchOn: Boolean
}