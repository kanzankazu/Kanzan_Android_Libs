@file:Suppress("MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanwidget.qrcode.widget

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.google.zxing.Result
import com.kanzankazu.kanzanutil.BaseConst
import com.kanzankazu.kanzanutil.PermissionUtil
import com.kanzankazu.kanzanutil.kanzanextension.PermissionEnumArray
import com.kanzankazu.kanzanutil.kanzanextension.isPermissions

/**
 * implementation 'com.budiyev.android:code-scanner:2.1.0'
 */
class KanzanQrScanner(
    private val activity: AppCompatActivity,
    private val scannerView: CodeScannerView,
    private val listener: Listener,
) {
    private var mCodeScanner: CodeScanner? = null
    private var androidPermissionUtil: PermissionUtil = PermissionUtil(activity)

    init {
        startScan()
        qrScannerInit(activity, scannerView)
    }

    /**
     * place in onStart or onResume
     * */
    fun startScan() {
        if (activity.isPermissions(PermissionEnumArray.CAMERA_FILE_ACCESS)) {

        }
        if (androidPermissionUtil.checkPermissionWithRequest(BaseConst.PERM_CAMERA_GALLERY)) {
            mCodeScanner?.startPreview()
        }
    }

    /**
     * place in onPause or onDestroy
     * */
    fun stopScan() {
        mCodeScanner?.stopPreview()
        mCodeScanner?.releaseResources()
    }

    private fun qrScannerInit(activity: Activity, scannerView: CodeScannerView) {
        mCodeScanner = CodeScanner(activity, scannerView)
        mCodeScanner?.setDecodeCallback { result ->
            activity.runOnUiThread {
                try {
                    listener.successScan(result)
                } catch (e: Exception) {
                    mCodeScanner?.startPreview()
                    listener.failedScan(e)
                }
            }
        }
    }

    interface Listener {
        fun successScan(result: Result)
        fun failedScan(e: Exception)
    }
}