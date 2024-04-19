package com.kanzankazu.kanzanutil

import android.Manifest
import com.kanzankazu.kanzanutil.kanzanextension.isTiramisuAbove
import java.util.Locale

object BaseConst {
    const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DATE_FORMAT_COMPLETE = "EEE, d MMM yyyy, HH:mm"
    const val DATE_FORMAT_COMPLETE_2 = "EEEE/MMMM yyy-MM-dd kk:mm:ss"
    const val DATE_FORMAT_STD_DATE = "yyyy-MM-dd"
    const val DATE_FORMAT_STD_TIME = "kk:mm:ss"
    const val DATE_FORMAT_DD = "dd"

    val REGEX_ALPHABETH_LO_UP = Regex("[a-zA-Z]+")
    val REGEX_ALPHABETH_LO = Regex("[a-z]+")
    val REGEX_ALPHABETH_UP = Regex("[A-Z]+")
    val REGEX_ALPHANUMERIC_LO_UP = Regex("[a-zA-Z0-9]+")
    val REGEX_ALPHANUMERIC_LO = Regex("[a-z0-9]+")
    val REGEX_ALPHANUMERIC_UP = Regex("[A-Z0-9]+")

    val PERM_CAMERA_GALLERY = if (isTiramisuAbove()) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA,
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }
    val PERM_LOCATION = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val PERM_RECORD_AUDIO = if (isTiramisuAbove()) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.RECORD_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }
    val PERM_READ_PHONE_STATE = arrayOf(
        Manifest.permission.READ_PHONE_STATE
    )
    val PERM_READ_SMS = arrayOf(
        Manifest.permission.READ_SMS
    )

    val locale = Locale("id", "ID")
}
