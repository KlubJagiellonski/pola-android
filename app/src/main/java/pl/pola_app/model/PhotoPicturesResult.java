package pl.pola_app.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoPicturesResult {
    @SerializedName("width")
    int width;
    @SerializedName("height")
    int height;
    @SerializedName("signed_requests")
    List<String> signedRequests;

    public int signedRequestsSize() {
        if (signedRequests == null) {
            return 0;
        }
        return signedRequests.size();
    }

    public String getSignedRequestAt(int index) {
        if (signedRequests == null) {
            return null;
        }
        return signedRequests.get(index);
    }

    @Override
    public String toString() {
        return "PhotoPicturesResult{" +
                "width=" + width +
                ", height=" + height +
                ", signedRequests=" + signedRequests +
                '}';
    }
}
