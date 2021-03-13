package pl.pola_app.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import pl.pola_app.model.Donate


@Parcel
data class SearchResult @ParcelConstructor constructor(val product_id: Int,
                                                       val code: String,
                                                       val name: String,
                                                       val card_type: String,
                                                       val plScore: Int?,
                                                       val altText: String?,
                                                       val plCapital: Int?,
                                                       val plWorkers: Int?,
                                                       val plRnD: Int?,
                                                       val plRegistered: Int?,
                                                       val plNotGlobEnt: Int?,
                                                       val is_friend: Boolean?,
                                                       val description: String?,
                                                       val report_text: String,
                                                       val report_button_text: String,
                                                       val report_button_type: String,
                                                       val friend_text: String?,
                                                       val donate: Donate?,
){
    fun askForSupport(): Boolean {
        return donate != null
    }
}