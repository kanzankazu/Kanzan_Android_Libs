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
 * Converts an array of strings from the resources into a list of strings.
 *
 * @param int The resource ID of the string array (annotated with `@ArrayRes`).
 * @return A list of strings populated from the specified string array resource.
 *
 * Example:
 * ```kotlin
 * val stringList = activity.getArrayStringsToList(R.array.example_array)
 * ```
 */
fun Activity.getArrayStringsToList(@ArrayRes int: Int): List<String> = resources.getStringArray(int).toList()

/**
 * Converts an array resource into a list of strings within a Fragment's context.
 * This method retrieves the string array from the given resource id, hosted by the parent Activity,
 * and converts it into a List<String>.
 *
 * @param int The resource id of the string array (annotated with @ArrayRes) to be converted into a list.
 *            It must be a reference to a valid string array resource.
 * @return A list of strings contained in the specified string array resource. Returns a non-null list.
 *         Throws an exception if the activity or resource context is not available.
 *
 * Example:
 * ```kotlin
 * val stringList = fragment.getArrayStringsToList(R.array.example_array)
 * // stringList will contain the elements of R.array.example_array as a List<String>
 * ```
 */
fun Fragment.getArrayStringsToList(@ArrayRes int: Int): List<String> = activity?.getArrayStringsToList(int)!!

/**
 * Displays a Snackbar message in an Activity. The message can either appear temporarily or remain indefinitely
 * until manually dismissed by the user.
 *
 * @param text The message content to be displayed in the Snackbar.
 * @param isForEver If set to true, the Snackbar will show indefinitely with a "close" action button. Defaults to false (temporary display).
 *
 * Example:
 * ```kotlin
 * // Show a temporary Snackbar
 * this.simpleSnackbar("This is a temporary message")
 *
 * // Show an indefinite Snackbar with close action
 * this.simpleSnackbar("This message stays unless closed", isForEver = true)
 * ```
 */
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

/**
 * Displays a simple Snackbar with the given text in the current Fragment's host Activity.
 *
 * @param text The message to display in the Snackbar.
 * @param isForEver A Boolean flag to indicate whether the Snackbar should remain open indefinitely.
 *                  If true, the Snackbar will remain visible until the "close" action is tapped.
 *                  Defaults to false, making the Snackbar visible only for a short duration.
 *
 * Example:
 * ```kotlin
 * fragment.simpleSnackbar("Operation completed successfully.")
 * fragment.simpleSnackbar("Error occurred!", isForEver = true)
 * ```
 */
fun Fragment.simpleSnackbar(text: CharSequence, isForEver: Boolean = false) {
    requireActivity().simpleSnackbar(text, isForEver)
}

/**
 * Opens the Telegram app to share a given message.
 * If the Telegram app is installed, it launches the app with the text ready to be shared.
 * If the app is not installed, a toast message will notify the user.
 *
 * @param msg The message that will be shared through Telegram. Must be a non-empty string.
 *
 * Example:
 * ```kotlin
 * val message = "Hello, Telegram!"
 * openTelegram(message)
 * ```
 */
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

/**
 * Refreshes the current `Activity` by recreating it, effectively resetting its state.
 *
 * This method internally calls the `recreate()` function to reload the current activity,
 * applying changes such as layout updates or configurations. All current activity data will
 * be reset unless explicitly preserved via other mechanisms.
 *
 * This method is useful in scenarios where you need to refresh the activity due to changes
 * in configuration, theme updates, or to ensure the UI reflects updated data.
 *
 * Usage:
 * ```kotlin
 * val activity: Activity = this
 * activity.refresh()
 * ```
 */
fun Activity.refresh() {
    /*this.finish()
    this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    this.startActivity(intent)
    this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/
    recreate()
}

/**
 * Checks if the device has a stable internet connection by attempting to establish a connection to a Google DNS server.
 * Performs the operation on a background thread and notifies the result on the main thread.
 *
 * @return A Single emitting `true` if the device is connected to the internet and the connection is stable,
 *         or `false` if the connection is unstable or not available.
 *
 * Example:
 * ```kotlin
 * val connectionObserver = hasStableInternetConnection()
 * connectionObserver.subscribe { isConnected ->
 *     if (isConnected) {
 *         println("Device has a stable internet connection!")
 *     } else {
 *         println("No stable connection available.")
 *     }
 * }
 * ```
 */
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

/**
 * Pings a given URL by attempting to establish a connection and checking its HTTP response code.
 * Converts "https" to "http" to bypass SSL issues for invalid certificates.
 *
 * @param url The URL to ping. The URL must follow a valid format (e.g., "http://example.com").
 * @param timeout The connection timeout period, in milliseconds, after which the attempt will be abandoned.
 * @return `true` if the response code is within the range of 200 to 399 (inclusive), signaling a successful connection; `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val isReachable = ping("http://example.com", 5000)
 * println(isReachable) // true if the server is reachable within the timeout
 * ```
 */
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

/**
 * Generates a WhatsApp chat link for a given phone number.
 *
 * @param phoneNumber The phone number (in international format, excluding '+' sign) to which the chat link should refer.
 * Ensure the phone number includes the country code (e.g., "1234567890" for "+1-234-567-890").
 * @return A String containing the WhatsApp API link for the specified phone number.
 *
 * Example:
 * ```kotlin
 * val phoneNumber = "1234567890" // International format phone number without '+'
 * val waLink = waFormat(phoneNumber) // "https://api.whatsapp.com/send?phone=1234567890"
 * ```
 */
fun waFormat(phoneNumber: String): String = "https://api.whatsapp.com/send?phone=$phoneNumber"
