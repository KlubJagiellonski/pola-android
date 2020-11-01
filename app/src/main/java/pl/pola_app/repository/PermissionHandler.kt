package pl.pola_app.repository

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


interface PermissionHandler {
    fun hasPermission(activity: Activity, permission: PermissionType): Boolean

    fun checkPermission(
        activity: Activity,
        permission: PermissionType,
        callback: (result: Result) -> Unit
    )

    fun checkPermissions(
        activity: Activity,
        permissions: List<PermissionType>,
        callback: () -> Unit
    )

    sealed class Result {
        object Granted : Result()
        data class Denied(val permanently: Boolean) : Result()
    }
}

class PermissionHandlerImpl : PermissionHandler {
    override fun checkPermissions(
        activity: Activity,
        permissions: List<PermissionType>,
        callback: () -> Unit
    ) {
        Dexter.withActivity(activity)
            .withPermissions(permissions.map { it.permissionCode })
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    callback()
                    token?.cancelPermissionRequest()
                }

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    callback()
                }
            }).onSameThread().check()
    }

    override fun hasPermission(activity: Activity, permission: PermissionType): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission.permissionCode
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun checkPermission(
        activity: Activity,
        permission: PermissionType,
        callback: (result: PermissionHandler.Result) -> Unit
    ) {
        Dexter.withActivity(activity)
            .withPermission(permission.permissionCode)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    callback(PermissionHandler.Result.Granted)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    callback(PermissionHandler.Result.Denied(response?.isPermanentlyDenied ?: false))
                }
            }).onSameThread().check()
    }
}


