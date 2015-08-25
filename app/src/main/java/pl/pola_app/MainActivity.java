package pl.pola_app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.Result;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import io.fabric.sdk.android.Fabric;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Product;
import pl.pola_app.network.ProductRequest;
import pl.pola_app.network.RetrofitSpiceService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler{

    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.fl_camera_container)
    FrameLayout cameraContainerFrameLayout;

    private ZXingScannerView scannerView;

    private SpiceManager spiceManager = new SpiceManager(RetrofitSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        scannerView = new ZXingScannerView(this);
        scannerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cameraContainerFrameLayout.addView(scannerView);
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        Timber.v(TAG, result.getText());
        Timber.v(TAG, result.getBarcodeFormat().toString());
        ProductRequest productRequest = new ProductRequest(result.getText(), Utils.getDeviceId(this));
        spiceManager.execute(productRequest, "product", DurationInMillis.ONE_HOUR, new ProductRequestListener());
    }

    private final class ProductRequestListener implements RequestListener<Product> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Product product) {
            Toast.makeText(MainActivity.this, "Cool", Toast.LENGTH_SHORT).show();
            Timber.v(TAG, product.code);
        }
    }
}
