package pl.pola_app.ui.fragment

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import com.journeyapps.barcodescanner.Size
import com.squareup.otto.Bus
import pl.pola_app.PolaApplication
import pl.pola_app.R
import pl.pola_app.helpers.URL_POLA_ABOUT
import pl.pola_app.ui.activity.ActivityWebView
import pl.pola_app.ui.event.FlashActionListener
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback
import timber.log.Timber
import javax.inject.Inject

class ScannerFragment : Fragment(), TorchListener, FlashActionListener {
    @kotlin.jvm.JvmField
    @Inject
    var eventBus: Bus? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.scanner_view)
    var barcodeScanner //ZXING this or mPreview should be used
            : CompoundBarcodeView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.appIcon)
    var appIcon: ImageView? = null
    override var isTorchOn = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        PolaApplication.Companion.component(activity)?.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val scannerView = inflater.inflate(R.layout.fragment_scanner, container, false)
        ButterKnife.bind(this, scannerView)
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        barcodeScanner?.barcodeView?.framingRectSize =
            Size((width * 0.9f).toInt(), (height * 0.25f).toInt())
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, (-1 * (height * 0.2)).toInt(), 0, 0)
        barcodeScanner?.layoutParams = layoutParams
        val cameraSettings = barcodeScanner?.barcodeView?.cameraSettings
        //cameraSettings.setBarcodeSceneModeEnabled(true);
        cameraSettings?.isContinuousFocusEnabled = true
        cameraSettings?.isAutoFocusEnabled = true
        barcodeScanner?.barcodeView?.cameraSettings = cameraSettings
        barcodeScanner?.setStatusText(activity.getString(R.string.scanner_status_text))
        barcodeScanner?.setTorchListener(this)
        barcodeScanner?.setTorchOff()
        Nammu.askForPermission(activity, Manifest.permission.CAMERA, permissionCameraCallback)
        appIcon?.setOnClickListener { view: View? ->
            val intent = Intent(activity, ActivityWebView::class.java)
            intent.putExtra("url", URL_POLA_ABOUT)
            startActivity(intent)
        }
        return scannerView
    }

    override fun onResume() {
        super.onResume()
        eventBus?.register(this)
        if (barcodeScanner != null) {
            barcodeScanner?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        eventBus?.unregister(this)
        if (barcodeScanner != null) {
            barcodeScanner?.setTorchOff()
            barcodeScanner?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun resumeScanning() {
        if (barcodeScanner != null) {
            barcodeScanner?.decodeContinuous(callback)
        }
    }

    val permissionCameraCallback: PermissionCallback = object : PermissionCallback {
        override fun permissionGranted() {
            resumeScanning()
        }

        override fun permissionRefused() {
            Toast.makeText(activity, getString(R.string.toast_no_camera_access), Toast.LENGTH_SHORT)
                .show()
        }
    }

    //ZXING barcode result
    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text != null) {
                if (barcodeScanner != null) {
                    barcodeScanner?.barcodeView?.stopDecoding()
                    barcodeScanner?.setStatusText("")
                }
                (activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(100)
                activity.runOnUiThread { onBarcode(result.text) }
                Timber.d(result.text)
                Timber.d(result.barcodeFormat.toString())
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    private fun onBarcode(barcode: String) {
        val activity = activity
        if (activity != null && !activity.isFinishing && activity is BarcodeListener) {
            (activity as BarcodeListener).onBarcode(barcode, true)
        }
    }

    override fun onFlashAction() {
        if (isTorchOn) {
            barcodeScanner?.setTorchOff()
        } else {
            barcodeScanner?.setTorchOn()
        }
    }

    override fun onTorchOn() {
        isTorchOn = true
    }

    override fun onTorchOff() {
        isTorchOn = false
    }

    fun setTorchOff() {
        if (isTorchOn) {
            barcodeScanner?.setTorchOff()
        }
    }
}