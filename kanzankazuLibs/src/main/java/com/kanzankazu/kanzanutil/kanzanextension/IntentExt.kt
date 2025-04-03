@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kanzankazu.kanzanutil.image.FileManager
import com.kanzankazu.kanzanutil.image.ImageCompressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

fun Intent.addActionView() = apply { action = Intent.ACTION_VIEW }

fun Intent.addClearTaskNewTask() = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

fun Intent.addFlagClearSingleTop() = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

fun Intent.changePageFromIntent(activity: ComponentActivity, bundle: Bundle? = null, finish: Boolean = false) {
    if (bundle != null) putExtras(bundle)
    activity.startActivity(this)
    if (finish) activity.finish()
}

fun Intent.changePageFromIntent(fragment: Fragment, bundle: Bundle? = null, finish: Boolean = false) {
    fragment.activity?.let {
        if (bundle != null) putExtras(bundle)
        it.startActivity(this)
        if (finish) it.finish()
    } ?: kotlin.run { return }
}

fun Intent?.isActionBootCompleted() =
    this?.action?.equals("android.intent.action.BOOT_COMPLETED") ?: false

fun Intent?.isActionSend() =
    this?.action?.equals("android.intent.action.SEND") ?: false

fun Intent?.isActionSendMultiple() =
    this?.action?.equals("android.intent.action.SEND_MULTIPLE") ?: false

fun Intent?.isTypeTextPlain() =
    this?.type?.equals("text/plain") ?: false

fun Intent?.isTypeImage() =
    this?.type?.startsWith("image/") ?: false

inline fun <reified T> Context.makeIntent() = Intent(this, T::class.java)

/**
 * Navigates from the current Activity to a new Activity of the specified type.
 * Allows passing additional configurations such as intent extras, whether the current Activity should be finished, custom activity options, and intent
 *  modifications.
 *
 * @param bundle The extras to add to the Intent for the new Activity. Defaults to null if no extras are provided.
 * @param finish If true, the current Activity will be finished after starting the new Activity. Defaults to false.
 * @param intent An optional Intent object to use for navigation. If null, a new Intent is created targeting the specified Activity type. Defaults to
 *  null.
 * @param optionBundle Additional activity options to pass when starting the Activity. Typically used for custom activity transitions. Defaults to null
 * .
 * @param intentHandle A lambda function to modify the Intent object before it is used for starting the Activity. Typically used to add custom flags
 *  or data to the Intent. Defaults to an empty lambda.
 *
 * Example:
 * ```kotlin
 * changePage<SecondActivity>(bundle = Bundle().apply { putString("key", "value") }, finish = true)
 * ```
 */
inline fun <reified T> Activity.changePage(bundle: Bundle? = null, finish: Boolean = false, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(this, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivity(newIntent, optionBundle)
    if (finish) finish()
}

/**
 * Navigates from the current Fragment to a specified target Fragment or Activity by creating and starting an Intent.
 * Allows for optional data passing via a Bundle, control over whether to finish the current Activity,
 * and customization of the Intent through the `intentHandle` lambda.
 *
 * @param bundle The data to pass to the target Activity as a Bundle. Defaults to null.
 * @param finish Indicates whether the current Activity should be finished after navigating. Defaults to false.
 * @param intent A pre-configured Intent for the navigation. If null, a new Intent will be created targeting the specified `T`. Defaults to null.
 * @param optionBundle Additional options to pass to `startActivity` as part of the transition. Defaults to null.
 * @param intentHandle A lambda function to customize the Intent before it is started. Defaults to an empty lambda.
 */
inline fun <reified T> Fragment.changePage(bundle: Bundle? = null, finish: Boolean = false, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(activity, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivity(newIntent, optionBundle)
    if (finish) activity?.finish()
}

/**
 * Launches an Activity of the specified type `T` for a result, with optional parameters for intent and bundles.
 *
 * @param requestCode The request code that will be returned in `onActivityResult` to identify the result.
 * @param bundle An optional Bundle containing extra data to pass to the target Activity. Defaults to `null`.
 * @param intent An optional Intent instance to use for launching the Activity. Defaults to `null`.
 * @param optionBundle An optional Bundle for additional Activity options, often used for shared element transitions. Defaults to `null`.
 * @param intentHandle A lambda function to configure the Intent before launching the Activity.
 *                     This allows further customization, like adding flags or setting data.
 *
 * Example:
 * ```kotlin
 * changePageForResult<MyActivity>(
 *     requestCode = 100,
 *     bundle = Bundle().apply { putString("key", "value") },
 *     intentHandle = { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
 * )
 * ```
 */
inline fun <reified T> Activity.changePageForResult(requestCode: Int, bundle: Bundle? = null, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(this, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivityForResult(newIntent, requestCode, optionBundle)
}

/**
 * Launches an activity of the specified type `T` from a `Fragment` while including additional configurations
 * like request code, extras, custom intent configuration, and an optional option bundle for animations or transitions.
 *
 * @param requestCode The request code to return in `onActivityResult` when the activity exits.
 * @param bundle An optional `Bundle` of extras to pass to the launched activity. Defaults to `null`.
 * @param intent An optional preconstructed `Intent` to be used for launching the activity. Defaults to `null`,
 * in which case a new intent targeting the `T` class will be created.
 * @param optionBundle An optional `Bundle` containing additional options for how the activity should be started (e.g., animations). Defaults to `null
 * `.
 * @param intentHandle A lambda function for further customization of the `Intent`. This lambda operates on the intent
 * before it is used to start the activity.
 *
 * Example:
 * ```kotlin
 * changePageForResult<MyActivity>(
 *    requestCode = 1001,
 *    bundle = Bundle().apply { putString("KEY", "value") },
 *    intentHandle = { putExtra("EXTRA_KEY", "extraValue") }
 * )
 * ```
 */
inline fun <reified T> Fragment.changePageForResult(requestCode: Int, bundle: Bundle? = null, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(activity, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivityForResult(newIntent, requestCode, optionBundle)
}

/**
 * Initializes the registration for an `ActivityResult` callback for starting an activity for a result.
 * The provided callback function is invoked with the `ActivityResult` when the result is received.
 *
 * @param result A callback function that takes an `ActivityResult` as a parameter. This is invoked when the activity result is available.
 *
 * Example:
 * ```kotlin
 * val launcher = changePageForResultInit { activityResult ->
 *     if (activityResult.resultCode == Activity.RESULT_OK) {
 *         // Handle the result
 *         val data = activityResult.data
 *     }
 * }
 * launcher.launch(intent) // Launch the activity with an intent
 * ```
 */
fun ComponentActivity.changePageForResultInit(result: (result: ActivityResult) -> Unit) =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result(it) }

/**
 * Initializes and registers a callback for handling Activity results in a Fragment.
 * The specified lambda function is invoked with the result of the activity.
 * If an exception occurs during registration, it is logged and a toast message displays the error.
 *
 * @param result A lambda function to process the result of the activity. It receives an `ActivityResult` object as the parameter.
 *
 * Example:
 * ```kotlin
 * fragment.changePageForResultInit { activityResult ->
 *     if (activityResult.resultCode == Activity.RESULT_OK) {
 *         // Handle successful result
 *     }
 * }
 * ```
 */
fun Fragment.changePageForResultInit(result: (result: ActivityResult) -> Unit) = try {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result(it) }
} catch (e: Exception) {
    e.printStackTrace()
    this.requireContext().simpleToast(e.message.toString())
    null
}

/**
 * Launches an intent for the specified destination activity with the provided bundle and intent,
 * using the given ActivityResultLauncher.
 *
 * @param activityResultLauncher The ActivityResultLauncher used to manage the result of the launched activity.
 * @param bundle Optional Bundle containing any key-value pairs to pass to the target activity. Defaults to null.
 * @param intent Optional Intent to customize the launch. If null, a new Intent targeting the specified activity class is created internally.
 *
 * Example:
 * ```kotlin
 * val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
 *     // Handle result
 * }
 * changePageForResultLaunch<MySecondActivity>(activityLauncher, bundle = myBundle)
 * ```
 */
inline fun <reified T> ComponentActivity.changePageForResultLaunch(activityResultLauncher: ActivityResultLauncher<Intent>, bundle: Bundle? = null, intent: Intent? = null) {
    val newIntent = intent ?: Intent(this, T::class.java)
    if (bundle != null) newIntent.putExtras(bundle)
    activityResultLauncher.launch(newIntent)
}

/**
 * Launches a new activity for a result using a specified ActivityResultLauncher, with optional parameters for intent and bundle.
 * The generic type `T` represents the target Activity class.
 *
 * @param activityResultLauncher The ActivityResultLauncher to launch the new activity.
 * @param bundle Optional data to pass to the new activity as extras. Defaults to null.
 * @param intent Optional custom Intent to use for launching the new activity. If null, a default Intent targeting the class `T` will be created.
 *
 * Example:
 * ```kotlin
 * val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
 *     // Handle the result here
 * }
 * fragment.changePageForResultLaunch<MyTargetActivity>(launcher, bundle = Bundle().apply {
 *     putString("key", "value")
 * })
 * ```
 */
inline fun <reified T> Fragment.changePageForResultLaunch(activityResultLauncher: ActivityResultLauncher<Intent>, bundle: Bundle? = null, intent: Intent? = null) {
    val newIntent = intent ?: Intent(activity, T::class.java)
    if (bundle != null) newIntent.putExtras(bundle)
    activityResultLauncher.launch(newIntent)
}

/**
 * Launches a new activity for a result using the provided ActivityResultLauncher,
 * and optionally attaches a bundle of additional data to the intent.
 *
 * @param activityResultLauncher The launcher to initiate the activity for a result.
 *                               This is an instance of ActivityResultLauncher<Intent>.
 * @param bundle (Optional) A Bundle to include with the intent as extras. Defaults to null.
 *
 * Example:
 * ```kotlin
 * val intent = Intent(this, TargetActivity::class.java)
 * val bundle = Bundle().apply { putString("key", "value") }
 * intent.changePageForResultLaunch(activityResultLauncher, bundle)
 * ```
 */
fun Intent.changePageForResultLaunch(activityResultLauncher: ActivityResultLauncher<Intent>, bundle: Bundle? = null) {
    val newIntent = this
    if (bundle != null) newIntent.putExtras(bundle)
    activityResultLauncher.launch(newIntent)
}

/**
 * Initializes and registers the `ActivityResultLauncher` for handling a single permission request.
 * The provided callback will be invoked with the result of the permission request as a Boolean.
 *
 * @param callBack The callback to be executed when the permission request result is received.
 *                 The Boolean indicates whether the permission was granted (`true`) or denied (`false`).
 */
fun ComponentActivity.requestPermissionLaunchInit(callBack: ActivityResultCallback<Boolean>) =
    registerForActivityResult(ActivityResultContracts.RequestPermission(), callBack)

/**
 * Registers a permission request callback to handle the result of a permission request.
 * This function is typically used in a Fragment to simplify the management of runtime permission requests.
 *
 * @param callBack A lambda function that will be invoked when the permission request result is received.
 *                 The lambda accepts a Boolean indicating whether the permission was granted (true) or denied (false).
 *
 * Example:
 * ```kotlin
 * class MyFragment : Fragment() {
 *     private val requestPermission = requestPermissionLaunchInit { isGranted ->
 *         if (isGranted) {
 *             // Handle permission granted
 *         } else {
 *             // Handle permission denied
 *         }
 *     }
 *
 *     fun askForPermission() {
 *         requestPermission.launch(Manifest.permission.CAMERA)
 *     }
 * }
 * ```
 */
fun Fragment.requestPermissionLaunchInit(callBack: ActivityResultCallback<Boolean>) =
    registerForActivityResult(ActivityResultContracts.RequestPermission(), callBack)

/**
 * @param permission [android.Manifest.permission]
 * */
fun ActivityResultLauncher<String>.requestPermissionLaunch(permission: String) {
    launch(permission)
}

fun ComponentActivity.requestPermissionMultipleLaunchInit(callBack: ActivityResultCallback<Map<String, Boolean>>): ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callBack)

fun Fragment.requestPermissionMultipleLaunchInit(callBack: ActivityResultCallback<Map<String, Boolean>>): ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callBack)

/**
 * @param permission [android.Manifest.permission]
 * */
fun ActivityResultLauncher<Array<String>>.requestPermissionMultipleLaunch(permission: Array<String>) {
    launch(permission)
}

fun Activity.forResult(isResultOk: Boolean, bundle: Bundle? = null) {
    if (bundle == null) {
        setResult(if (isResultOk) Activity.RESULT_OK else Activity.RESULT_CANCELED)
    } else {
        val intent = Intent().putExtras(bundle)
        setResult(if (isResultOk) Activity.RESULT_OK else Activity.RESULT_CANCELED, intent)
    }
    finish()
}

fun Activity.forResult(resultCode: Int, bundle: Bundle? = null) {
    if (bundle == null) {
        setResult(resultCode)
    } else {
        val intent = Intent().putExtras(bundle)
        setResult(resultCode, intent)
    }
    finish()
}

fun Fragment.forResult(isResultOk: Boolean, bundle: Bundle? = null) {
    activity?.forResult(isResultOk, bundle)
}

fun Fragment.forResult(resultCode: Int, bundle: Bundle? = null) {
    activity?.forResult(resultCode, bundle)
}

fun Activity.isChangePageForResult() = callingActivity != null

fun Activity.getTransAnimBundle(view: View, sharedElementName: String) =
    ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, sharedElementName).toBundle()

fun Activity.getTransAnimBundle(pair: Pair<View, String>) =
    ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()

fun handleSendText(intent: Intent) = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""

fun Context.handleSendImage(intent: Intent) =
    (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM))?.let { imageUri ->
        val inputStream = contentResolver.openInputStream(imageUri as Uri)
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpeg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        Pair(file, imageUri)
    }

fun Context.handleSendMultipleImages(intent: Intent) =
    intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let { imageUris ->
        val files = ArrayList<Pair<File, Uri>>()

        imageUris.forEachIndexed { index, imageUri ->
            val inputStream = contentResolver.openInputStream(imageUri as Uri)
            val file =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_$index.jpeg")
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            files.add(Pair(file, imageUri))
        }
        files
    } ?: arrayListOf()

fun AppCompatActivity.intentPickVisualMediaInit(): ActivityResultLauncher<PickVisualMediaRequest> {
    val imageCompressor = ImageCompressor(context = applicationContext)
    val fileManager = FileManager(context = applicationContext)

    val scope = CoroutineScope(this.lifecycleScope.coroutineContext)

    return registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { contentUri ->
        if (contentUri == null) return@registerForActivityResult

        val mimeType = contentResolver.getType(contentUri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        scope.launch {
            fileManager.saveImage(
                contentUri = contentUri,
                fileName = "uncompressed.$extension"
            )
        }

        scope.launch {
            val compressedImage = imageCompressor.compressImage(
                contentUri = contentUri,
                compressionThreshold = 200 * 1024L
            )
            fileManager.saveImage(
                bytes = compressedImage ?: return@launch,
                fileName = "compressed.$extension"
            )
        }
    }
}