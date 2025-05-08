package com.kanzankazu.kanzanutil.kanzanextension

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Represents the state of a permission.
 *
 *  * `GRANTED`: The permission has been granted by the user.
 *  * `DENIED`: The permission has been denied by the user and the user can be asked again.
 *  * `SHOW_RATIONALE`: The permission has been denied by the user and the user should be shown a rationale for why the permission is needed before asking again. This state typically occurs after a permission has been denied once.
 *  * `PERMANENTLY_DENIED`: The permission has been permanently denied by the user, and the user will not be asked again within the app. The user may need to manually grant the permission in system settings.
 */
enum class PermissionState {
    GRANTED, DENIED, SHOW_RATIONALE, PERMANENTLY_DENIED
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiramisuAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun FragmentActivity.getPermissionState(permission: PermissionEnum): PermissionState {
    return when {
        ContextCompat.checkSelfPermission(this, permission.permission) == PackageManager.PERMISSION_GRANTED -> PermissionState.GRANTED
        !shouldShowRequestPermissionRationale(permission.permission) &&
                ContextCompat.checkSelfPermission(this, permission.permission) == PackageManager.PERMISSION_DENIED -> PermissionState.PERMANENTLY_DENIED

        shouldShowRequestPermissionRationale(permission.permission) -> PermissionState.SHOW_RATIONALE
        else -> PermissionState.DENIED
    }
}

fun FragmentActivity.getPermissionState(permissions: Array<PermissionEnum>): PermissionState {
    return when {
        permissions.all { ContextCompat.checkSelfPermission(this, it.permission) == PackageManager.PERMISSION_GRANTED } -> PermissionState.GRANTED
        permissions.any {
            !shouldShowRequestPermissionRationale(it.permission) &&
                    ContextCompat.checkSelfPermission(this, it.permission) == PackageManager.PERMISSION_DENIED
        } -> PermissionState.PERMANENTLY_DENIED

        permissions.any { shouldShowRequestPermissionRationale(it.permission) } -> PermissionState.SHOW_RATIONALE
        else -> PermissionState.DENIED
    }
}

fun FragmentActivity.getPermissionState(permissions: PermissionEnumArray): PermissionState {
    return getPermissionState(permissions.permissions) // Reuse the existing function
}

fun FragmentActivity.getPermissionState(permissions: Array<PermissionEnumArray>): PermissionState {
    return getPermissionState(permissions.convertToArray()) // Reuse the existing function (after fix)
}

@JvmOverloads
fun FragmentActivity.requestPermissions(permissions: PermissionEnumArray, activityResultLauncher: ActivityResultLauncher<Array<String>>? = null, requestCode: Int = 0, isWithDialog: Boolean = true) {
    requestPermissions(permissions.permissions, activityResultLauncher, requestCode, isWithDialog)
}

@JvmOverloads
fun FragmentActivity.requestPermissions(permissions: Array<PermissionEnum>, activityResultLauncher: ActivityResultLauncher<Array<String>>? = null, requestCode: Int = 0, isWithDialog: Boolean = true) {
    requestPermissions(permissions.toArrayString(), activityResultLauncher, requestCode, isWithDialog)
}

@JvmOverloads
fun FragmentActivity.requestPermissions(permissions: Array<String>, activityResultLauncher: ActivityResultLauncher<Array<String>>? = null, requestCode: Int = 0, isWithDialog: Boolean = true) {
    fun launchPermissionsRequest(activityResultLauncher: ActivityResultLauncher<Array<String>>?, permissions: Array<String>, requestCode: Int) {
        if (activityResultLauncher != null) activityResultLauncher.launch(permissions)
        else ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    val isRationale = permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }

    if (isRationale && isWithDialog) {
        val message = when {
            permissions.contentEquals(PermissionEnumArray.POST_NOTIFICATIONS.permissions.toArrayString()) -> "Kami membutuhkan izin notifikasi untuk mengunduh memberi tahu anda jika ada notifikasi terbaru"
            permissions.contentEquals(PermissionEnumArray.CAMERA.permissions.toArrayString()) -> "Kami membutuhkan izin kamera untuk membuka foto"
            permissions.contentEquals(PermissionEnumArray.FILE_ACCESS.permissions.toArrayString()) -> "Kami membutuhkan izin penyimpanan untuk meletakan unduhan data"
            permissions.contentEquals(PermissionEnumArray.CAMERA_FILE_ACCESS.permissions.toArrayString()) -> "Kami membutuhkan izin kamera dan penyimpanan untuk membuka foto dan menyimpan foto"
            permissions.contentEquals(PermissionEnumArray.LOCATION.permissions.toArrayString()) -> "Kami membutuhkan izin lokasi untuk mencari lokasi anda untuk penyediaan data di sekitar anda"
            else -> ""
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ -> launchPermissionsRequest(activityResultLauncher, permissions, requestCode) }
        builder.setNegativeButton("BATAL") { _, _ -> }
        builder.show()
    } else launchPermissionsRequest(activityResultLauncher, permissions, requestCode)
}

fun FragmentActivity.permissionResultHandler(callback: (Map<String, Boolean>) -> Unit) =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callback)

fun Array<PermissionEnumArray>.convertToArray(): Array<PermissionEnum> {
    val hasil = mutableListOf<PermissionEnum>()
    for (array in this) hasil.addAll(array.permissions)
    return hasil.toTypedArray()
}

fun Array<PermissionEnumArray>.toArrayString(): Array<String> {
    val hasil = mutableListOf<PermissionEnum>()
    for (array in this) hasil.addAll(array.permissions)
    return hasil.toTypedArray().toArrayString()
}

fun PermissionEnumArray.toArrayString(): Array<String> = this.permissions.toArrayString()

fun Array<PermissionEnum>.toArrayString(): Array<String> = this.map { it.permission }.toTypedArray()

fun FragmentActivity.openPermissionSettings(requestCode: Int? = null) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    customTabsIntent.intent.data = Uri.fromParts("package", packageName, null)
    customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    requestCode?.let { startActivityForResult(customTabsIntent.intent, it) } ?: run { startActivity(customTabsIntent.intent) }
}

enum class PermissionEnum(val permission: String) {
    CAMERA("android.permission.CAMERA"),
    INTERNET("android.permission.INTERNET"),
    READ_PHONE_STATE("android.permission.READ_PHONE_STATE"),
    READ_CONTACTS("android.permission.READ_CONTACTS"),
    ACCESS_FINE_LOCATION("android.permission.ACCESS_FINE_LOCATION"),
    ACCESS_COARSE_LOCATION("android.permission.ACCESS_COARSE_LOCATION"),
    WRITE_EXTERNAL_STORAGE("android.permission.WRITE_EXTERNAL_STORAGE"),
    READ_EXTERNAL_STORAGE("android.permission.READ_EXTERNAL_STORAGE"),
    POST_NOTIFICATIONS("android.permission.POST_NOTIFICATIONS"),
    READ_MEDIA_AUDIO("android.permission.READ_MEDIA_AUDIO"),
    READ_MEDIA_IMAGES("android.permission.READ_MEDIA_IMAGES"),
    READ_MEDIA_VIDEO("android.permission.READ_MEDIA_VIDEO"),
    RECORD_AUDIO("android.permission.RECORD_AUDIO"),
    READ_SMS("android.permission.READ_SMS"),
}

enum class PermissionEnumArray(val permissions: Array<PermissionEnum>) {
    POST_NOTIFICATIONS(
        arrayOf(
            PermissionEnum.POST_NOTIFICATIONS
        )
    ),
    CAMERA(
        arrayOf(
            PermissionEnum.CAMERA
        )
    ),
    CAMERA_FILE_ACCESS(
        if (isTiramisuAbove()) {
            arrayOf(
                PermissionEnum.CAMERA,
                PermissionEnum.READ_MEDIA_IMAGES,
                PermissionEnum.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                PermissionEnum.CAMERA,
                PermissionEnum.WRITE_EXTERNAL_STORAGE,
                PermissionEnum.READ_EXTERNAL_STORAGE
            )
        }
    ),
    LOCATION(
        arrayOf(
            PermissionEnum.ACCESS_FINE_LOCATION,
            PermissionEnum.ACCESS_COARSE_LOCATION
        )
    ),
    FILE_ACCESS(
        if (isTiramisuAbove()) {
            arrayOf(
                PermissionEnum.READ_MEDIA_AUDIO,
                PermissionEnum.READ_MEDIA_IMAGES,
                PermissionEnum.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                PermissionEnum.WRITE_EXTERNAL_STORAGE,
                PermissionEnum.READ_EXTERNAL_STORAGE
            )
        }
    ),
}
