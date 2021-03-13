package pl.pola_app.model

import rx.Observable
import java.util.*

data class PhotoPicturesSendData internal constructor(private val width: Int, private val height: Int) {
    private val fileToSendDataList: MutableList<FileToSendData> = LinkedList()
    fun createFileToSendDataObservable(): Observable<FileToSendData> {
        return Observable.from(fileToSendDataList)
    }

    companion object {
        fun create(
            photoPicturesResult: PhotoPicturesResult,
            files: List<TakenPicInfo>
        ): PhotoPicturesSendData {
            val size = photoPicturesResult.signedRequestsSize()
            require(size == files.size) { "PhotoPicturesResult is not compatible with file list - different size!" }
            val result =
                PhotoPicturesSendData(photoPicturesResult.width, photoPicturesResult.height)
            for (index in 0 until size) {
                result.fileToSendDataList.add(
                    FileToSendData(
                        files[index],
                        photoPicturesResult.getSignedRequestAt(index)
                    )
                )
            }
            return result
        }
    }
}