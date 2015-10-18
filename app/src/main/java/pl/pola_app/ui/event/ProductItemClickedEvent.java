package pl.pola_app.ui.event;

import pl.pola_app.model.Product;

/**
 * Created by grzegorzkapusta on 08.10.2015.
 */
public class ProductItemClickedEvent {
    public Product product;

    public ProductItemClickedEvent(Product item) {
        this.product = item;
    }

}
