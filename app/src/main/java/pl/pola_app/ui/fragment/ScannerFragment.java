package pl.pola_app.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Product;
import pl.pola_app.network.GetProductRequest;
import pl.pola_app.ui.event.ProductRequestSuccessEvent;
import timber.log.Timber;

public class ScannerFragment extends Fragment implements RequestListener<Product> {

    private static final String REQUEST_CACHE_KEY = "request_cache_key";

    @Inject
    SpiceManager spiceManager;

    @Inject
    Bus eventBus;

    @Bind(R.id.scanner_view)
    CompoundBarcodeView barcodeScanner;

    private GetProductRequest productRequest;

    public ScannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View scannerView = inflater.inflate(R.layout.fragment_scanner, container, false);
        ButterKnife.bind(this, scannerView);
        PolaApplication.component(getActivity()).inject(this);

        barcodeScanner.setStatusText(getActivity().getString(R.string.scanner_status_text));
        barcodeScanner.decodeContinuous(callback);

        return scannerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(productRequest != null) {
            outState.putString(REQUEST_CACHE_KEY, productRequest.getCacheKey());
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            String spiceRequestCacheKey = savedInstanceState.getString(REQUEST_CACHE_KEY);
            if (spiceRequestCacheKey != null) {
                spiceManager.addListenerIfPending(Product.class, spiceRequestCacheKey, this);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScanner.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScanner.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                Timber.d(result.getText());
                Timber.d(result.getBarcodeFormat().toString());

                productRequest = new GetProductRequest(result.getText(), Utils.getDeviceId(getActivity()));
                spiceManager.execute(productRequest, productRequest.getCacheKey(), DurationInMillis.ONE_HOUR, ScannerFragment.this);
                barcodeScanner.setStatusText("");
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(getActivity(), spiceException.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(Product product) {
        eventBus.post(new ProductRequestSuccessEvent(product));
    }
}
