package pl.pola_app.ui.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.CameraCompat;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.FileUtils;
import pl.pola_app.model.SearchResult;
import pl.pola_app.presenter.VideoCapturePresenter;
import pl.pola_app.presenter.view.VideoCaptureView;
import pl.pola_app.ui.activity.CreateReportActivity;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class VideoCaptureFragment extends Fragment implements VideoCaptureView, SurfaceHolder.Callback {

    private static final String SEARCH_RESULT = "SEARCH_RESULT";
    private static final String DEVICE_ID = "DEVICE_ID";
    @Inject
    VideoCapturePresenter videoCapturePresenter;

    @Bind(R.id.video_capture_button)
    Button videoCaptureButton;

    @Bind(R.id.video_capture_timer)
    TextView timer;


    @Bind(R.id.video_capture_surface_view)
    SurfaceView videoCaptureSurfaceView;

    ProgressDialog progressDialog;
    private EventLogger logger;
    private SearchResult searchResult;
    private String deviceId;

    final PermissionCallback permissionCameraCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
        }

        @Override
        public void permissionRefused() {
            getActivity().finish();
        }
    };

    public static VideoCaptureFragment newInstance(SearchResult searchResult, String deviceId) {
        final VideoCaptureFragment result = new VideoCaptureFragment();
        final Bundle args = new Bundle();
        args.putParcelable(SEARCH_RESULT, Parcels.wrap(searchResult));
        args.putString(DEVICE_ID, deviceId);
        result.setArguments(args);
        return result;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        PolaApplication.component(getContext()).inject(this);
        super.onCreate(savedInstanceState);
        videoCapturePresenter.initView(this);
        final Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalArgumentException("Please pass SearchResult as fragment args");
        }
        searchResult = Parcels.unwrap(arguments.getParcelable(SEARCH_RESULT));
        deviceId = arguments.getString(DEVICE_ID);
        videoCapturePresenter.onCreate(searchResult, deviceId);
        logger = new EventLogger(getContext());
        logger.logLevelStart("aipics", searchResult.code, deviceId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_capture, container, false);
    }

    CameraCompat cameraCompat;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        if(args == null) {
            throw new IllegalArgumentException("Please pass SearchResult as fragment argument");
        }
        final SearchResult searchResult = Parcels.unwrap(args.getParcelable(SEARCH_RESULT));
        cameraCompat = CameraCompat.create(searchResult.maxPicSize());
        cameraCompat.open();
        videoCaptureSurfaceView.getHolder().addCallback(this);
        timer.setText(searchResult.askForPicsProduct());
        Nammu.askForPermission(getActivity(), new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        } , permissionCameraCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraCompat.openPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraCompat.closePreview();
    }

    @Override
    public void onDestroy() {
        videoCapturePresenter.onDestroy();
        videoCapturePresenter = null;
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void updateTimer(long time) {
        cameraCompat.takePicture((bitmap,originalWidth, originalHeight, width, height) -> videoCapturePresenter.onTakePicture(bitmap, time, originalWidth, originalHeight, width, height));
        timer.setText(videoCapturePresenter.createTimerLabel(getActivity(), time));
    }

    @Override
    public void updateStopButton() {
        videoCaptureButton.setVisibility(View.GONE);
        videoCaptureButton.setText(R.string.video_capture_stop);
    }

    @Override
    public void onVideoCapturingFinish() {
        final SearchResult searchResult = Parcels.unwrap(getArguments().getParcelable(SEARCH_RESULT));
        timer.setText(searchResult.askForPicsButtonStart());
        timer.setVisibility(View.GONE);
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.sending_image_dialog), true);
        videoCaptureButton.setVisibility(View.VISIBLE);
        videoCaptureButton.setText(R.string.video_capture_start);
    }

    @Override
    public void onPhotosUploadFinish() {
        logger.logLevelEnd("aipics", searchResult.code, deviceId);

        final FragmentActivity activity = getActivity();
        if (activity != null) {
            Toast.makeText(getActivity(), R.string.thank_you_for_teaching, Toast.LENGTH_LONG).show();
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
        if(progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    @OnClick(R.id.video_capture_button)
    public void onVideoCaptureButtonClick() {
        timer.setVisibility(View.VISIBLE);
        videoCapturePresenter.onCaptureButtonClick();
    }
}
