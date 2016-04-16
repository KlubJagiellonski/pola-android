package pl.pola_app.ui.fragment;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.ui.activity.ActivityWebView;
import pl.pola_app.ui.activity.CreateReportActivity;
import pl.pola_app.ui.event.ProductItemClickedEvent;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import timber.log.Timber;

public class ScannerFragment extends Fragment implements CompoundBarcodeView.TorchListener {
    private static final String TAG = ScannerFragment.class.getSimpleName();

    public interface BarcodeScannedListener {
        void barcodeScanned(String result);
    }

    private BarcodeScannedListener barcodeScannedListener;

    @Inject
    Bus eventBus;

    @Bind(R.id.textHintScan)
    TextView textHintScan;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.scanner_view)
    CompoundBarcodeView barcodeScanner;//ZXING this or mPreview should be used

    @Bind(R.id.flash_icon)
    ImageView flashIconView;

    private boolean isTorchOn = false;

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

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        barcodeScanner.getBarcodeView().setFramingRectSize(new Size((int) (width*0.9f), (int) (height*0.25f)));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, (int) (-1*(height*0.2)), 0, 0);
        barcodeScanner.setLayoutParams(layoutParams);

        CameraSettings cameraSettings = barcodeScanner.getBarcodeView().getCameraSettings();
        cameraSettings.setBarcodeSceneModeEnabled(true);
        barcodeScanner.getBarcodeView().setCameraSettings(cameraSettings);

        barcodeScanner.setStatusText(getActivity().getString(R.string.scanner_status_text));
        barcodeScanner.setTorchListener(this);
        barcodeScanner.setTorchOff();

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
        eventBus.register(this);
        if(barcodeScanner != null) {
            barcodeScanner.resume();
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        eventBus.unregister(this);
        if(barcodeScanner != null) {
            barcodeScanner.setTorchOff();
            barcodeScanner.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void resumeScanning() {
        if(barcodeScanner != null) {
            barcodeScanner.decodeContinuous(callback);
        }
    }

    public void updateBoxPosition(int numberOfCards) {
    }

    final PermissionCallback permissionCameraCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            resumeScanning();
        }

        @Override
        public void permissionRefused() {
            Toast.makeText(getActivity(), getString(R.string.toast_no_camera_access), Toast.LENGTH_SHORT).show();
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
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Utils.POLA_MAIL, null));
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
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_FB));
                startActivity(intent);
                return true;
            case R.id.action_twitter:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.URL_POLA_TWITTER));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //ZXING barcode result
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {
            if (result.getText() != null) {
                if(barcodeScanner != null) {
                    barcodeScanner.getBarcodeView().stopDecoding();
                    barcodeScanner.setStatusText("");
                }
                ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (barcodeScannedListener != null) {
                            barcodeScannedListener.barcodeScanned(result.getText());
                        }
                    }
                });

                Timber.d(result.getText());
                Timber.d(result.getBarcodeFormat().toString());

            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @OnClick(R.id.flash_icon)
    public void onFlashIconClicked() {
        if(isTorchOn) {
            barcodeScanner.setTorchOff();
        } else {
            barcodeScanner.setTorchOn();
        }
    }

    @Override
    public void onTorchOn() {
        isTorchOn = true;
        if(flashIconView != null) {
            flashIconView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_flash_off_white_48dp));
        }
    }

    @Override
    public void onTorchOff() {
        isTorchOn = false;
        if(flashIconView != null) {
            flashIconView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_flash_on_white_48dp));
        }
    }

    @Subscribe
    public void productItemClicked(ProductItemClickedEvent event) {
        if(isTorchOn) {
            barcodeScanner.setTorchOff();
        }
    }
}
