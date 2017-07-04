package pl.pola_app.model;


public class TakenPicInfo {
    private final String fileName;
    private final int originalWidth;
    private final int originalHeight;
    private final int width;
    private final int height;

    public TakenPicInfo(String fileName, int originalWidth, int originalHeight, int width, int height) {
        this.fileName = fileName;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.width = width;
        this.height = height;
    }

    public String getFileName() {
        return fileName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }
}
