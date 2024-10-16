package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import android.content.Intent
import androidx.annotation.ArrayRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

/**
 * @param int R.array.xxxx
 * */
fun Activity.getArrayStringsToList(@ArrayRes int: Int): List<String> = resources.getStringArray(int).toList()

fun Fragment.getArrayStringsToList(@ArrayRes int: Int): List<String> = activity?.getArrayStringsToList(int)!!

/*fun Context.simpleToast(text: CharSequence) {
    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
}*/

fun Activity.simpleSnackbar(text: CharSequence, isForEver: Boolean = false) {
    if (isForEver) Snackbar
        .make(findViewById(android.R.id.content), text, if (isForEver) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_SHORT)
        .setAction("close", {})
        .setActionTextColor(ContextCompat.getColor(this, R.color.baseWhite))
        .setTextColor(ContextCompat.getColor(this, R.color.baseWhite))
        .show()
    else Snackbar
        .make(findViewById(android.R.id.content), text, if (isForEver) Snackbar.LENGTH_SHORT else Snackbar.LENGTH_SHORT)
        .show()
}

fun Fragment.simpleSnackbar(text: CharSequence, isForEver: Boolean = false) {
    requireActivity().simpleSnackbar(text, isForEver)
}

fun Activity.openTelegram(msg: String) {
    val appName = "org.telegram.messenger"
    val isAppInstalled: Boolean = appInstalledOrNot(appName)
    if (isAppInstalled) {
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "text/plain"
        myIntent.setPackage(appName)
        myIntent.putExtra(Intent.EXTRA_TEXT, msg) //
        startActivity(Intent.createChooser(myIntent, "Share with"))
    } else {
        simpleToast("Telegram not Installed")
    }
}

fun Activity.refresh() {
    /*this.finish()
    this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    this.startActivity(intent)
    this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/
    recreate()
}

fun hasStableInternetConnection(): Single<Boolean> {
    return Single.fromCallable {
        try {
            // Connect to Google DNS to check for connection
            val timeoutMs = 10000
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)

            socket.connect(socketAddress, timeoutMs)
            socket.close()

            true
        } catch (e: IOException) {
            "hasStableInternetConnection ${e.message}".debugMessageDebug()
            false
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

@Suppress("ConvertTwoComparisonsToRangeCheck")
fun ping(url: String, timeout: Int): Boolean {
    var url1 = url
    url1 = url1.replaceFirst("https".toRegex(), "http") // Otherwise an exception may be thrown on invalid SSL certificates.
    return try {
        val connection: HttpURLConnection = URL(url1).openConnection() as HttpURLConnection
        connection.connectTimeout = timeout
        connection.readTimeout = timeout
        connection.requestMethod = "HEAD"
        val responseCode: Int = connection.responseCode
        200 <= responseCode && responseCode <= 399
    } catch (exception: IOException) {
        false
    }
}

fun waFormat(phoneNumber: String): String = "https://api.whatsapp.com/send?phone=$phoneNumber"
