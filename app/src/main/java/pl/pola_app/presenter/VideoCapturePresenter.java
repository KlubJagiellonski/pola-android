package pl.pola_app.presenter;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.FileUtils;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.FileToSendData;
import pl.pola_app.model.PhotoPicturesReport;
import pl.pola_app.model.PhotoPicturesSendData;
import pl.pola_app.model.SearchResult;
import pl.pola_app.model.TakenPicInfo;
import pl.pola_app.network.Api;
import pl.pola_app.presenter.view.VideoCaptureView;
import rx.Observable;
import rx.schedulers.Schedulers;

public class VideoCapturePresenter {

    private static final String TAG = VideoCapturePresenter.class.getSimpleName();
    private static final String MIME_TYPE = "image/*";

    private final StringBuilder stringBuilder;

    private VideoCaptureView view;

    private final List<TakenPicInfo> fileNames = new LinkedList<>();

    final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    private final CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
            view.updateTimer(seconds);
        }

        @Override
        public void onFinish() {
            view.onVideoCapturingFinish();
        }
    };

    SearchResult searchResult;
    String deviceId;

    public void onCreate(SearchResult searchResult, String deviceId) {
        this.searchResult = searchResult;
        this.deviceId = deviceId;
    }

    @Inject
    VideoCapturePresenter() {
        stringBuilder = new StringBuilder();
    }

    public void initView(VideoCaptureView view) {
        this.view = view;
    }

    public void onCaptureButtonClick() {
        countDownTimer.start();
        view.updateStopButton();
        view.updateTimer(6);
    }


    public String createTimerLabel(Context context, long time) {
        stringBuilder.setLength(0);
        return stringBuilder
                .append(time)
                .append(" ")
                .append(context.getString(R.string.seconds_left))
                .append(time < 5 ? context.getString(time < 2 ? R.string.seconds_a : R.string.seconds_y) : "")
                .toString();
    }

    public void onDestroy() {
        countDownTimer.cancel();
        view = null;
    }

    public void sendFilesToServer(List<TakenPicInfo> list) {
        final PhotoPicturesReport photoPicturesReport = PhotoPicturesReport.builder()
                .setDeviceName(Utils.getDeviceName())
                .setFilesCount(list.size())
                .setOriginalWidth(list.get(0).getOriginalWidth())
                .setOriginalHeight(list.get(0).getOriginalHeight())
                .setWidth(list.get(0).getWidth())
                .setHeight(list.get(0).getHeight())
                .setProductId(searchResult.product_id)
                .build();
        PolaApplication.retrofit.create(Api.class).addAiPics(deviceId, photoPicturesReport)
                .flatMap(it -> Observable.just(PhotoPicturesSendData.create(it, list)))
                .flatMap(PhotoPicturesSendData::createFileToSendDataObservable)
                .flatMap(this::sendFileToServer)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        it -> {/*Do nothing*/},
                        throwable -> Log.e(TAG, "Error occours", throwable),
                        () -> clearState(list)
                );
    }

    private void clearState(List<TakenPicInfo> list) {
        for (TakenPicInfo takenPicInfo : list) {
            if (new File(takenPicInfo.getFileName()).delete()) {
                Log.d(TAG, "File : " + takenPicInfo.getFileName() + " was removed");
            }
        }
        if (view != null) {
            new Handler(Looper.getMainLooper()).post((view::onPhotosUploadFinish));
        }
    }

    private Observable<Void> sendFileToServer(FileToSendData data) {
        File imageFile = new File(data.getTakenPicInfo().getFileName());
        RequestBody photoBody = RequestBody.create(MediaType.parse(MIME_TYPE), imageFile);
        Log.d(TAG, data.getDestinationUrl());
        return PolaApplication.retrofit.create(Api.class).sendReportImageAsObservable(data.getDestinationUrl(), photoBody);
    }

    public void onTakePicture(Bitmap bitmap, long time, int originalWidth, int originalHeight, int width, int height) {
        threadPoolExecutor.execute(() -> {
            final String picturePath = Environment.getExternalStorageDirectory() + "/test" + time + ".jpg";
            FileUtils.saveBitmap(bitmap, picturePath);
            fileNames.add(new TakenPicInfo(picturePath, originalWidth, originalHeight, width, height));
            Log.d(TAG, "File " + picturePath + " is saved o thread " + Thread.currentThread().getName());
            if (time == 1) {
                sendFilesToServer(fileNames);
            }
        });
    }
}
