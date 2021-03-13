package pl.pola_app.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel
data class Ai(
    val ask_for_pics: Boolean = false,
    val ask_for_pics_preview: String? = null,
    val ask_for_pics_title: String? = null,
    val ask_for_pics_text: String? = null,
    val ask_for_pics_button_start: String? = null,
    val ask_for_pics_product: String? = null,
    val max_pic_size: Int = 0,
)