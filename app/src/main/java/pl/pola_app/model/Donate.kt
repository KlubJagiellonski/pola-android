package pl.pola_app.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel
data class Donate(
    val show_button: Boolean = false,
    val url: String = "",
    val title: String = "",
)