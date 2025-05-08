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
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kanzankazu.kanzanutil.image.FileManager
import com.kanzankazu.kanzanutil.image.ImageCompressor
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

fun Intent.getStringExtraDefault(name: String, default: String = "") = getStringExtra(name) ?: default

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

inline fun <reified T : Fragment> T.createFragmentWithArgs(intentHandle: Bundle.() -> Unit): T {
    val args = Bundle()
    intentHandle.invoke(args)
    val fragment = this
    fragment.arguments = args
    return fragment
}

inline fun<reified T : Fragment> T.handleFragmentArgs(listener: Bundle.() -> Unit) {
    arguments?.let { listener.invoke(it) }
}

inline fun <reified T> Context.makeIntent() = Intent(this, T::class.java)

inline fun <reified T> Activity.changePage(bundle: Bundle? = null, finish: Boolean = false, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(this, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivity(newIntent, optionBundle)
    if (finish) finish()
}

inline fun <reified T> Fragment.changePage(bundle: Bundle? = null, finish: Boolean = false, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(activity, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivity(newIntent, optionBundle)
    if (finish) activity?.finish()
}

inline fun <reified T> Activity.changePageForResult(requestCode: Int, bundle: Bundle? = null, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(this, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivityForResult(newIntent, requestCode, optionBundle)
}

inline fun <reified T> Fragment.changePageForResult(requestCode: Int, bundle: Bundle? = null, intent: Intent? = null, optionBundle: Bundle? = null, intentHandle: Intent.() -> Unit = {}) {
    val newIntent = intent ?: Intent(activity, T::class.java)
    newIntent.apply { intentHandle.invoke(this) }
    if (bundle != null) newIntent.putExtras(bundle)
    startActivityForResult(newIntent, requestCode, optionBundle)
}

fun ComponentActivity.changePageForResultInit(result: (result: ActivityResult) -> Unit) =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result(it) }

fun Fragment.changePageForResultInit(result: (result: ActivityResult) -> Unit) = try {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result(it) }
} catch (e: Exception) {
    e.debugMessageError("Fragment.changePageForResultInit")
    this.requireContext().simpleToast(e.message.toString())
    null
}

inline fun <reified T> ComponentActivity.changePageForResultLaunch(activityResultLauncher: ActivityResultLauncher<Intent>, bundle: Bundle? = null, intent: Intent? = null) {
    val newIntent = intent ?: Intent(this, T::class.java)
    if (bundle != null) newIntent.putExtras(bundle)
    activityResultLauncher.launch(newIntent)
}

inline fun <reified T> Fragment.changePageForResultLaunch(activityResultLauncher: ActivityResultLauncher<Intent>, bundle: Bundle? = null, intent: Intent? = null) {
    val newIntent = intent ?: Intent(activity, T::class.java)
    if (bundle != null) newIntent.putExtras(bundle)
    activityResultLauncher.launch(newIntent)
}

fun Intent.changePageForResultLaunch(activityResultLauncher: ActivityResultLauncher<Intent>, bundle: Bundle? = null) {
    val newIntent = this
    if (bundle != null) newIntent.putExtras(bundle)
    activityResultLauncher.launch(newIntent)
}

fun ComponentActivity.requestPermissionLaunchInit(callBack: ActivityResultCallback<Boolean>) =
    registerForActivityResult(ActivityResultContracts.RequestPermission(), callBack)

fun Fragment.requestPermissionLaunchInit(callBack: ActivityResultCallback<Boolean>) =
    registerForActivityResult(ActivityResultContracts.RequestPermission(), callBack)

fun ActivityResultLauncher<String>.requestPermissionLaunch(permission: String) {
    launch(permission)
}

fun ComponentActivity.requestPermissionMultipleLaunchInit(callBack: ActivityResultCallback<Map<String, Boolean>>): ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callBack)

fun Fragment.requestPermissionMultipleLaunchInit(callBack: ActivityResultCallback<Map<String, Boolean>>): ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callBack)

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