package pl.pola_app.presenter.view;


public interface VideoCaptureView {

    void updateTimer(long time);
    void updateStopButton();
    void onVideoCapturingFinish();
    void onPhotosUploadFinish();
}
