@file:Suppress("unused")

package com.kanzankazu.kanzanwidget.camera.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CameraProperty(
    var idUser: String? = null,
    var type: String? = null,
    var title: String? = null,
    var pictUrl: String? = null,
) : Parcelable