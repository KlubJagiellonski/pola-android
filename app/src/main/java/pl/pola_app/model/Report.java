package pl.pola_app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tajchert on 28.10.2015.
 */
public class Report {
    @SerializedName("description")
    String description;
    @SerializedName("product_id")
    String productId;

    public Report(String description, String productId) {
        this.description = description;
        this.productId = productId;
    }

    public Report(String description) {
        this.description = description;
    }
}
