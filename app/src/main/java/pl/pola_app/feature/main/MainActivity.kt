package pl.pola_app.feature.main

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import androidx.activity.viewModels
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import com.journeyapps.barcodescanner.Size
import pl.pola_app.BuildConfig
import pl.pola_app.R
import pl.pola_app.databinding.ActivityMainBinding
import pl.pola_app.feature.base.BaseActivity
import pl.pola_app.feature.browser.BrowserActivity
import pl.pola_app.feature.common.ClickListener
import pl.pola_app.feature.digit.DigitActivity
import pl.pola_app.feature.menu.MenuActivity
import pl.pola_app.repository.PermissionHandler
import pl.pola_app.repository.PermissionType
import pl.pola_app.utils.dpToPx
import javax.inject.Inject


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main), TorchListener {

    @Inject
    lateinit var permissionHandler: PermissionHandler

    override val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun initObservers() {
        viewModel.torchOn.observe(this, {
            if (it) {
                binding.scannerView.setTorchOn()
            } else {
                binding.scannerView.setTorchOff()
            }
        })

        viewModel.barcodeList.observe(this, {
            if (it.isNotEmpty()) {
                vibrate()

                binding.listAdapter?.apply {
                    this.list = it
                    this.notifyDataSetChanged()
                }
            }
        })
    }

    override fun initBaseData() {

    }

    override fun initListeners() {
        binding.logo.setOnClickListener {
            BrowserActivity.start(this, BuildConfig.URL_POLA_ABOUT)
        }

        binding.flashIcon.setOnClickListener {
            viewModel.torchOn.value = viewModel.torchOn.value?.not()
        }

        binding.menu.setOnClickListener {
            MenuActivity.start(this@MainActivity)
        }

        binding.openKeyboardButton.setOnClickListener {
            DigitActivity.start(this)
        }
    }

    override fun initView() {
        binding.viewModel = viewModel

        binding.listAdapter = BarcodeAdapter(listOf(), object : ClickListener<String> {
            override fun onClick(item: String) {

            }
        }
        ).apply {
            setHasStableIds(true)
        }

        val metrics = DisplayMetrics()
        this.display?.getMetrics(metrics)

        val width = metrics.widthPixels
        val height = metrics.heightPixels

        binding.scannerView.barcodeView.framingRectSize = Size(
            (width * 0.9f).toInt(), (height * 0.25f).toInt()
        )

        binding.scannerView.barcodeView.cameraSettings.apply {
            isContinuousFocusEnabled = true
            isAutoFocusEnabled = true
        }

        binding.scannerView.apply {
            setTorchListener(this@MainActivity)
            setTorchOff()
        }
    }

    override fun initAnimations() {
        binding.logo.apply {
            val actionbarSize = 100.dpToPx()
            translationY = -actionbarSize

            animate()
                .translationY(0.0f)
                .setDuration(300)
                .startDelay = 300
        }

        binding.flashIcon.apply {
            val actionbarSize = 100.dpToPx()
            translationY = -actionbarSize

            animate()
                .translationY(0.0f)
                .setDuration(300)
                .startDelay = 600
        }

        binding.menu.apply {
            val actionbarSize = 100.dpToPx()
            translationY = -actionbarSize

            animate()
                .translationY(0.0f)
                .setDuration(300)
                .startDelay = 900
        }
    }

    override fun onResume() {
        super.onResume()

        permissionHandler.checkPermission(
            this,
            PermissionType.CAMERA
        ) { result ->
            when (result) {
                PermissionHandler.Result.Granted -> {
                    binding.scannerView.resume()
                    resumeScanning()
                }
                else -> {

                }
            }
        }
    }

    private fun resumeScanning() {
        binding.scannerView.decodeContinuous(callback)
    }

    override fun onTorchOn() {

    }

    override fun onTorchOff() {

    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (!result.text.isNullOrEmpty()) {
                viewModel.addNewBarcodeIfNotExits(result.text)
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    private fun vibrate() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
}