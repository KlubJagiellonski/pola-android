package pl.pola_app.model;


public class FileToSendData {
    private final TakenPicInfo takenPicInfo;
    private final  String destinationUrl;

    public FileToSendData(TakenPicInfo takenPicInfo, String destionationUrl) {
        this.takenPicInfo = takenPicInfo;
        this.destinationUrl = destionationUrl;
    }

    public TakenPicInfo getTakenPicInfo() {
        return takenPicInfo;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }
}
