package pl.pola_app.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Product;
import pl.pola_app.network.GetProductRequest;
import pl.pola_app.ui.events.ProductRequestSuccessEvent;
import timber.log.Timber;

public class ScannerFragment extends Fragment implements ZXingScannerView.ResultHandler, RequestListener<Product> {

    @Inject
    SpiceManager spiceManager;

    @Inject
    ZXingScannerView zXingView;

    @Inject
    Context appContext;

    @Inject
    Bus eventBus;

    @Bind(R.id.fl_camera_container)
    FrameLayout cameraContainerFrameLayout;

    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    public ScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View scannerView = inflater.inflate(R.layout.fragment_scanner, container, false);
        ButterKnife.bind(this, scannerView);
        PolaApplication.component(getActivity()).inject(this);

        zXingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cameraContainerFrameLayout.addView(zXingView);

        return scannerView;
    }

    @Override
    public void onStart() {
        spiceManager.start(appContext);
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
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        Timber.d(result.getText());
        Timber.d(result.getBarcodeFormat().toString());
        GetProductRequest productRequest = new GetProductRequest(result.getText(), Utils.getDeviceId(appContext));
        spiceManager.execute(productRequest, productRequest.getCacheKey(), DurationInMillis.ONE_HOUR, this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(appContext, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(Product product) {
        eventBus.post(new ProductRequestSuccessEvent(product));
    }
}
