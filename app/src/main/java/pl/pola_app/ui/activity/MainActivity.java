package pl.pola_app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Product;
import pl.pola_app.network.GetProductRequest;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler {

    @Inject
    SpiceManager spiceManager;

    @Inject
    ZXingScannerView zXingView;

    @Bind(R.id.fl_camera_container)
    FrameLayout cameraContainerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PolaApplication.component(this).inject(this);
        ButterKnife.bind(this);

        zXingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cameraContainerFrameLayout.addView(zXingView);
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        zXingView.setResultHandler(this);
        zXingView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingView.stopCamera();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        Timber.d(result.getText());
        Timber.d(result.getBarcodeFormat().toString());
        GetProductRequest productRequest = new GetProductRequest(result.getText(), Utils.getDeviceId(this));
        spiceManager.execute(productRequest, productRequest.getCacheKey(), DurationInMillis.ONE_HOUR, new ProductRequestListener());
    }

    private final class ProductRequestListener implements RequestListener<Product> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Product product) {
            Toast.makeText(MainActivity.this, "Cool", Toast.LENGTH_SHORT).show();
            Timber.d(product.code);
        }
    }
}
