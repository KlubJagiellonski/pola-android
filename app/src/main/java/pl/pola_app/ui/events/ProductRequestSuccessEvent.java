package pl.pola_app.ui.events;

import pl.pola_app.model.Product;

/**
 * Created by grzegorzkapusta on 29.08.2015.
 */
public class ProductRequestSuccessEvent {

    private Product product;

    public ProductRequestSuccessEvent(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
