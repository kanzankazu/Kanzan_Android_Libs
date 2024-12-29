@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION")

package com.kanzankazu.kanzanutil

import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import java.util.Collections

class PermissionUtil {
    val rpAccess = 123
    private var currentpermission: Array<String> = arrayOf()
    private var fragment: Fragment? = null
    private var activity: Activity

    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor(activity: Activity, fragment: Fragment?) {
        this.activity = activity
        this.fragment = fragment
    }

    constructor(activity: Activity, isInitCheckPermission: Boolean, permissions: Array<String>) {
        this.activity = activity
        currentpermission = permissions
        if (isInitCheckPermission) checkPermissionWithRequest(permissions)
    }

    constructor(activity: Activity, fragment: Fragment?, isInitCheckPermission: Boolean, permissions: Array<String>) {
        this.activity = activity
        this.fragment = fragment
        currentpermission = permissions
        if (isInitCheckPermission) checkPermissionWithRequest(permissions)
    }

    fun checkPermissionWithRequest(permissions: Array<String>): Boolean {
        currentpermission = permissions
        return if (checkPermissionOnly(permissions)) {
            true
        } else {
            requestPermission()
            false
        }
    }

    fun checkPermissionOnly(permissions: Array<String>): Boolean {
        currentpermission = permissions
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val listStat = ArrayList<String?>()
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) listStat.add("1") else listStat.add("0")
            }
            Collections.frequency(listStat, "1") == permissions.size
        } else {
            true
        }
    }

    fun requestPermission() {
        fragment?.requestPermissions(currentpermission, rpAccess) ?: kotlin.run { ActivityCompat.requestPermissions(activity, currentpermission, rpAccess) }
    }

    /**
     * call when onRequestPermissionsResult
     *
     * @param isFinish     jika semua izin tidak di berikan akan keluar activity
     * @param listener
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    fun onRequestPermissionsResult(isFinish: Boolean, listener: PermissionUtilListener, requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        return if (requestCode == rpAccess) {
            Log.d("Lihat", "checkResultPermission AndroidPermissionUtil : " + grantResults.size)
            Log.d("Lihat", "checkResultPermission AndroidPermissionUtil : " + permissions.size)
            val listStat = ArrayList<String?>()
            if (grantResults.isNotEmpty() && permissions.size == grantResults.size) {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        listStat.add("1")
                    } else {
                        listStat.add("0")
                    }
                }
            }
            val frequency1 = Collections.frequency(listStat, "1")
            if (frequency1 != grantResults.size) {
                "PermissionUtil - onRequestPermissionsResult".debugMessageDebug()
                val alertDialogBuilder = AlertDialog.Builder(activity)
                alertDialogBuilder.setMessage("you denied some permission, you must give all permission to next proccess?")
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton("Yes") { _: DialogInterface?, _: Int -> checkPermissionWithRequest(permissions) }
                alertDialogBuilder.setNegativeButton("No") { _: DialogInterface?, _: Int ->
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.error_permission_denied), Snackbar.LENGTH_SHORT).show()
                    if (isFinish) activity.finish()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
                listener.onPermissionDenied(activity.getString(R.string.error_permission_denied))
                false
            } else {
                listener.onPermissionGranted()
                true
            }
        } else {
            listener.onPermissionDenied(activity.getString(R.string.error_permission_denied))
            false
        }
    }

    interface PermissionUtilListener {
        fun onPermissionGranted()
        fun onPermissionDenied(message: String)
    }

    companion object
}
