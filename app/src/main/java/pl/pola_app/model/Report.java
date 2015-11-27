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
    @SerializedName("files_count")
    int filesCount;
    @SerializedName("mime_type")
    String mimeType;
    @SerializedName("file_ext")
    String fileExt;

    public Report(String description, String productId, int filesCount, String mimeType, String fileExt) {
        this(description, filesCount, mimeType, fileExt);
        this.productId = productId;
    }

    public Report(String description, int filesCount, String mimeType, String fileExt) {
        this.description = description;
        this.filesCount = filesCount;
        this.mimeType = mimeType;
        this.fileExt = fileExt;
    }
}
