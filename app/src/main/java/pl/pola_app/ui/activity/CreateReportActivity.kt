package pl.pola_app.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource
import pl.pola_app.PolaApplication
import pl.pola_app.R
import pl.pola_app.helpers.*
import pl.pola_app.helpers.SessionId.Companion.create
import pl.pola_app.model.Report
import pl.pola_app.model.ReportResult
import pl.pola_app.network.Api
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.*

class CreateReportActivity : Activity(), Callback<ReportResult> {
    private var productId: String = ""
    private var code: String = ""
    private val photoMarginDp = 6
    private var progressDialog: ProgressDialog? = null
    private var numberOfImages = 0
    private var reportResultCall: Call<ReportResult>? = null
    private lateinit var sessionId: SessionId

    @kotlin.jvm.JvmField
    @BindView(R.id.descripton_editText)
    var descriptionEditText: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.linearImageViews)
    var linearImageViews: LinearLayout? = null
    var bitmaps: ArrayList<Bitmap> = ArrayList()
    var bitmapsPaths: ArrayList<String> =
        ArrayList() //As we save file, it would be good to delete them after we send them
    private lateinit var logger: EventLogger 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_report)
        ButterKnife.bind(this)
        sessionId = create(this)
        if (intent != null) {
            productId = intent.getStringExtra("productId") ?: ""
            code = intent.getStringExtra("code") ?: ""
        }
        setImageView(bitmaps)
        Nammu.init(this)
        logger = EventLogger(this)
        logger.logLevelStart("report", code, sessionId.get())
    }

    override fun onPause() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.cancel()
        }
        super.onPause()
    }

    private fun setImageView(bitmapsToSet: ArrayList<Bitmap>?) {
        val margin = photoMarginDp.dpToPx()
        linearImageViews?.removeAllViews()
        var showAddButton = true
        if (bitmapsToSet != null && bitmapsToSet.size > 0) {
            for ((i, bitmap) in bitmapsToSet.withIndex()) {
                val imageView = ImageView(this)
                val layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                layoutParams.rightMargin = if (i == MAX_IMAGE_COUNT) 0 else margin
                layoutParams.weight = 1f
                imageView.layoutParams = layoutParams
                imageView.setOnClickListener { showDialogDeletePhoto(bitmapsToSet.indexOf(bitmap)) }
                imageView.setImageBitmap(bitmap)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                linearImageViews?.addView(imageView)
            }
            showAddButton = bitmapsToSet.size <= MAX_IMAGE_COUNT
        }
        if (showAddButton) {
            //Add add button
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.weight = 1f
            imageView.layoutParams = layoutParams
            imageView.setOnClickListener {
                Log.d(TAG, "onClick: ")
                launchCamera()
            }
            imageView.setImageBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_add_black_24dp
                )
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            linearImageViews?.addView(imageView)
        }
    }

    private fun showDialogDeletePhoto(position: Int) {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    if (position < bitmaps.size) {
                        bitmaps.removeAt(position)
                        setImageView(bitmaps)
                    }
                    if (position < bitmapsPaths.size) {
                        bitmapsPaths.removeAt(position)
                    }
                }
            }
        }
        val builder = AlertDialog.Builder(this@CreateReportActivity)
        builder.setMessage(getString(R.string.dialog_delete_photo))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
            .show()
    }

    private fun launchCamera() {
        val permissions =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        Nammu.askForPermission(this@CreateReportActivity, permissions, permissionCallback)
    }

    @OnClick(R.id.send_button)
    fun clickSendButton() {
        val description = descriptionEditText?.text.toString()
        sendReport(description, productId)
    }

    private fun sendReport(description: String, productId: String?) {
        if (productId == null && (bitmapsPaths.size == 0)) {
            Toast.makeText(
                this@CreateReportActivity,
                getString(R.string.toast_raport_error_no_pic),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        numberOfImages = bitmapsPaths.size
        //get ext from path
        val report: Report =
            productId?.let { Report(description, it, numberOfImages, MIME_TYPE, FILE_EXT) }
                ?: Report(description, numberOfImages, MIME_TYPE, FILE_EXT)
        val api: Api = PolaApplication.Companion.retrofit.create<Api>(
            Api::class.java
        )
        reportResultCall = api.createReport(sessionId.get(), report)
        reportResultCall?.enqueue(this)
        progressDialog = ProgressDialog.show(
            this@CreateReportActivity,
            "",
            getString(R.string.sending_image_dialog),
            true
        )
        logger.logLevelEnd("report", code, sessionId.get())
    }

    override fun onResponse(call: Call<ReportResult>, response: Response<ReportResult>) {
        Log.d(TAG, "onResponse: ")
        if (response.isSuccessful) {
            if (response.body() != null &&
                response.body()?.signed_requests?.size == bitmapsPaths.size
            ) {
                if (bitmapsPaths.size > 0) {
                    numberOfImages = 0
                    for (i in bitmapsPaths?.indices) {
                        val path = bitmapsPaths[i]
                        val url = response.body()?.signed_requests?.get(i)?.get(0)
                        url?.let { sendImage(path, it) }
                    }
                } else {
                    showEndResult(true)
                }
            } else {
                showEndResult(false)
            }
        } else {
            showEndResult(false)
        }
    }

    private fun showEndResult(isSuccess: Boolean) {
        var toastMessage = getString(R.string.toast_send_raport)
        if (!isSuccess) {
            toastMessage = getString(R.string.toast_send_raport_error)
        }
        Toast.makeText(this@CreateReportActivity, toastMessage, Toast.LENGTH_LONG).show()
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.cancel()
        }
        if (isSuccess) {
            finish()
        }
    }

    override fun onFailure(call: Call<ReportResult?>, t: Throwable?) {
        Log.d(TAG, "onFailure: ")
        if (progressDialog?.isShowing == true) {
            progressDialog?.cancel()
        }
        Timber.e(
            t,
            "Problem with photo report sending - this throwable cached and it is not fatal but app works wrong."
        )
        Toast.makeText(
            this@CreateReportActivity,
            getString(R.string.toast_send_raport_error),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun sendImage(imagePath: String, url: String) {
        //TODO tutaj
        numberOfImages++
        val api: Api = PolaApplication.Companion.retrofit.create<Api>(
            Api::class.java
        )
        val imageFile = File(imagePath)
        val photoBody = RequestBody.create(MediaType.parse(MIME_TYPE), imageFile)
        val reportResultCall = api.sendReportImage(url, photoBody)
        reportResultCall.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "onResponse image")
                val photoFile = File(imagePath)
                photoFile.delete()
                numberOfImages--
                if (numberOfImages == 0) {
                    showEndResult(true)
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.d(TAG, "onFailure image")
                numberOfImages--
                if (numberOfImages == 0) {
                    showEndResult(false)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setImageView(bitmaps)
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.cancel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onImagePicked(file: File, imageSource: ImageSource, type: Int) {
                    onPhotoReturned(file)
                }

                override fun onImagePickerError(e: Exception, source: ImageSource, type: Int) {
                    Toast.makeText(
                        this@CreateReportActivity,
                        getString(R.string.toast_raport_error_no_photo),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onPhotoReturned(file: File) {
        var bitmapPhoto: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inDither = true
            bitmapPhoto = BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: OutOfMemoryError) {
            Toast.makeText(
                this@CreateReportActivity,
                getString(R.string.toast_raport_error_no_memory),
                Toast.LENGTH_LONG
            ).show()
        }
        val photoPath = file.absolutePath
        if (!bitmapsPaths.contains(photoPath)) {
            bitmapsPaths.add(photoPath)
        }
        bitmapPhoto?.let {
            if (it.height > 1000 || it.width > 1000) {
                val aspectRatio = it.width / it.height.toFloat()
                var width = 1000
                var height = Math.round(width / aspectRatio)
                overrideImageLowRes(it, width, height, photoPath)
                width = 200
                height = Math.round(width / aspectRatio)
                bitmapPhoto =
                    Bitmap.createScaledBitmap(it, width, height, false) //TO use for upload
            }
            bitmaps.add(it)
            setImageView(bitmaps)
        }
    }

    private fun overrideImageLowRes(decoded: Bitmap, width: Int, height: Int, photoPath: String) {
        val bitmapToSave =
            Bitmap.createScaledBitmap(decoded, width, height, false) //To use as a thumbnail
        val dest = File(photoPath)
        try {
            val out = FileOutputStream(dest)
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 70, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteFiles(paths: ArrayList<String>) {
        for (path in paths) {
            val photoFile = File(path)
            photoFile.delete()
        }
        bitmaps.clear()
        bitmapsPaths.clear()
    }

    override fun onDestroy() {
        if (bitmapsPaths.size > 0) {
            deleteFiles(bitmapsPaths)
        }
        if (reportResultCall != null) {
            reportResultCall?.cancel()
        }
        super.onDestroy()
    }

    val permissionCallback: PermissionCallback = object : PermissionCallback {
        override fun permissionGranted() {
            EasyImage.openCamera(this@CreateReportActivity, 0)
        }

        override fun permissionRefused() {
            Toast.makeText(
                this@CreateReportActivity,
                getString(R.string.toast_no_camera_access),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private val TAG = CreateReportActivity::class.java.simpleName
        private const val MAX_IMAGE_COUNT = 2
        private const val MIME_TYPE = "image/jpg"
        private const val FILE_EXT = "jpg" //EasyImage captures jpegs
    }
}