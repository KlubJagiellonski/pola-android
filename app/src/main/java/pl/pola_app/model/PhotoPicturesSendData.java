package pl.pola_app.model;



import java.util.LinkedList;
import java.util.List;

import rx.Observable;

public class PhotoPicturesSendData{

    private final int width;
    private final int height;

    private final List<FileToSendData> fileToSendDataList = new LinkedList<>();

    PhotoPicturesSendData(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Observable<FileToSendData> createFileToSendDataObservable() {
        return Observable.from(fileToSendDataList);
    }

    public static PhotoPicturesSendData create(PhotoPicturesResult photoPicturesResult, List<TakenPicInfo> files) {
        final int size = photoPicturesResult.signedRequestsSize();
        if(size != files.size()) {
            throw new IllegalArgumentException("PhotoPicturesResult is not compatible with file list - different size!");
        }
        final PhotoPicturesSendData result = new PhotoPicturesSendData(photoPicturesResult.width, photoPicturesResult.height);
        for(int index = 0; index < size; index ++) {
            result.fileToSendDataList.add(new FileToSendData(files.get(index), photoPicturesResult.getSignedRequestAt(index)));
        }
        return result;

    }
}
