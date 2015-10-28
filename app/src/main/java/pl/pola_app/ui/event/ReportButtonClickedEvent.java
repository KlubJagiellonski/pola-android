package pl.pola_app.ui.event;

import pl.pola_app.model.Product;

public class ReportButtonClickedEvent {
    public Product product;

    public ReportButtonClickedEvent(Product product) {
        this.product = product;
    }
}
