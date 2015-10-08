package pl.pola_app.ui.event;

import android.support.v7.widget.CardView;

import com.squareup.otto.Produce;

/**
 * Created by grzegorzkapusta on 08.10.2015.
 */
public class CardClickedEvent {
    public CardView productCard;
    public int itemPosition;

    public CardClickedEvent(CardView productCard, int itemPosition) {
        this.productCard = productCard;
        this.itemPosition = itemPosition;
    }

}
