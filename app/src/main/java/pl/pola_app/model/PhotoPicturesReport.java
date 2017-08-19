package pl.pola_app.model;


import com.google.gson.annotations.SerializedName;

public class PhotoPicturesReport {

    @SerializedName("product_id")
    int productId;
    @SerializedName("files_count")
    int filesCount;
    @SerializedName("file_ext")
    String fileExt;
    @SerializedName("mime_type")
    String mineType;
    @SerializedName("original_width")
    int originalWidth;
    @SerializedName("original_height")
    int originalHeight;
    @SerializedName("width")
    int width;
    @SerializedName("height")
    int height;
    @SerializedName("device_name")
    String deviceName;
    @SerializedName("flash_used")
    boolean flashUsed;
    @SerializedName("was_portrait")
    boolean wasPortrait;

    public static class Builder {
        int productId;
        int filesCount;
        int originalWidth;
        int originalHeight;
        int width;
        int height;
        String deviceName;
        protected Builder() {

        }

        public Builder setProductId(int productId) {
            this.productId = productId;
            return  this;
        }

        public Builder setFilesCount(int filesCount) {
            this.filesCount = filesCount;
            return  this;
        }

        public Builder setOriginalWidth(int originalWidth) {
            this.originalWidth = originalWidth;
            return  this;
        }

        public Builder setOriginalHeight(int originalHeight) {
            this.originalHeight = originalHeight;
            return  this;
        }

        public Builder setDeviceName(String deviceName) {
            this.deviceName = deviceName;
            return  this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public PhotoPicturesReport build(){
            PhotoPicturesReport result = new PhotoPicturesReport();
            result.productId = productId;
            result.filesCount = filesCount;
            result.fileExt = "jpg";
            result.mineType = "image/*";
            result.originalWidth = originalWidth;
            result.originalHeight = originalHeight;
            result.width = width;
            result.height = height;
            result.deviceName = deviceName;
            result.flashUsed = false;
            result.wasPortrait = true;
            return  result;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
