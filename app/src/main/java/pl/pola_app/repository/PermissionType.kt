package pl.pola_app.repository

import android.Manifest

enum class PermissionType(val permissionCode: String) {
    CAMERA(Manifest.permission.CAMERA),
    READ_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
}
