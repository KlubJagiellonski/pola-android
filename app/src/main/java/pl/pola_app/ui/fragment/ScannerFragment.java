package pl.pola_app.ui.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.squareup.otto.Bus;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.CameraSourcePreview;
import pl.pola_app.helpers.Utils;
import pl.pola_app.ui.activity.ActivityWebView;
import pl.pola_app.ui.activity.CreateReportActivity;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import timber.log.Timber;

public class ScannerFragment extends Fragment {
    private static final String TAG = ScannerFragment.class.getSimpleName();

    public interface BarcodeScannedListener {
        void barcodeScanned(String result);
    }

    private BarcodeScannedListener barcodeScannedListener;

    @Inject
    Bus eventBus;

    @Bind(R.id.preview)
    CameraSourcePreview mPreview;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private CameraSource mCameraSource;
    private boolean isDecoding = true;
    private static final int RC_HANDLE_GMS = 9001;

    public ScannerFragment() {
        // Required empty public constructor
    }

    public void setOnBarcodeScannedListener(BarcodeScannedListener barcodeScannedListener) {
        this.barcodeScannedListener = barcodeScannedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View scannerView = inflater.inflate(R.layout.fragment_scanner, container, false);
        ButterKnife.bind(this, scannerView);
        PolaApplication.component(getActivity()).inject(this);

        //barcodeScanner.setStatusText(getActivity().getString(R.string.scanner_status_text));
        Nammu.askForPermission(getActivity(), android.Manifest.permission.CAMERA, permissionCameraCallback);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        setHasOptionsMenu(true);

        return scannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
        ButterKnife.unbind(this);
    }

    private void scannedBarcode(Barcode barcode) {
        final String result = barcode.displayValue;
        if (result != null) {
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (barcodeScannedListener != null) {
                        barcodeScannedListener.barcodeScanned(result);
                    }
                }
            });

            Timber.d(result);
        }
    }

    private void createCameraSource() {
        Context context = getActivity().getApplicationContext();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.d(TAG, "release: ");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                if (detections != null && detections.getDetectedItems() != null
                        && detections.getDetectedItems().size() > 0
                        && isDecoding) {
                    for (int i = 0; i < detections.getDetectedItems().size(); i++) {
                        int key = detections.getDetectedItems().keyAt(i);
                        Barcode barcode = detections.getDetectedItems().get(key);
                        Log.d(TAG, "receiveDetections barcode at:" + key + ", " + barcode);
                        if (barcode != null) {
                            scannedBarcode(barcode);
                        }
                    }
                    isDecoding = false;
                }
            }
        });
        if (!barcodeDetector.isOperational()) {
            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getActivity().registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Log.d(TAG, "createCameraSource error ");
                //Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                //Log.w(TAG, getString(R.string.low_storage_error));
            }
        }
        int width = 0;
        int height = 0;
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        } catch (Exception e) {
            //Something went wrong, use default values  (HD)
            width = 720;
            height = 1280;
        }
        if(width <= 0) {
            width = 720;
        }
        if(height <= 0) {
            height = 1280;
        }
        CameraSource.Builder builder = new CameraSource.Builder(getActivity().getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(width, height)
                .setRequestedFps(15.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setAutoFocusEnabled(true);
        }
        mCameraSource = builder.build();
    }

    private void startCameraSource() throws SecurityException {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        } else {
            //TODO zxing?
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public void resumeScanning() {
        isDecoding = true;
    }


    final PermissionCallback permissionCameraCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            createCameraSource();
        }

        @Override
        public void permissionRefused() {
            Toast.makeText(getActivity(), getString(R.string.toast_no_camera_access),  Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_about:
                intent = new Intent(getActivity(), ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_ABOUT);
                startActivity(intent);
                return true;
            case R.id.action_metodology:
                intent = new Intent(getActivity(), ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_METHOD);
                startActivity(intent);
                return true;
            case R.id.action_club:
                intent = new Intent(getActivity(), ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_KJ);
                startActivity(intent);
                return true;
            case R.id.action_team:
                intent = new Intent(getActivity(), ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_TEAM);
                startActivity(intent);
                return true;
            case R.id.action_partners:
                intent = new Intent(getActivity(), ActivityWebView.class);
                intent.putExtra("url", Utils.URL_POLA_PARTNERS);
                startActivity(intent);
                return true;
            case R.id.action_bug:
                intent = new Intent(getActivity(), CreateReportActivity.class);
                intent.setAction("product_report");
                startActivity(intent);
                return true;
            case R.id.action_mail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",Utils.POLA_MAIL, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pola");
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_picker)));
                return true;
            case R.id.action_rate:
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
                return true;
            case R.id.action_fb:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse( Utils.URL_POLA_FB));
                startActivity(intent);
                return true;
            case R.id.action_twitter:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse( Utils.URL_POLA_TWITTER));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
