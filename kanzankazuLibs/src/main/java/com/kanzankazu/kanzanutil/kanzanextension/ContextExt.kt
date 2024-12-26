package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.kanzankazu.R
import java.io.ByteArrayOutputStream
import java.io.File


fun Context.getColorFromResource(colorId: Int): Int =
    ContextCompat.getColor(this, colorId)

fun Context.getDrawableInt(drawableId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableId)

fun Context.emailHandler(email: String) {
    listOf("Copy Email", "Open Email").setupOptionItemListDialog(this) { _, position ->
        when (position) {
            0 -> this.clipboardCopy(email)
            1 -> this.openEmail(email)
        }
    }
}

fun Context.phoneHandler(phone: String) {
    listOf("Copy Phone Number", "Open Whatsapp", "Open Contact").setupOptionItemListDialog(this) { _, position ->
        when (position) {
            0 -> this.clipboardCopy(phone)
            1 -> this.openWhatsapp(waFormat(phone))
            2 -> this.openCall(phone)
        }
    }
}

fun Context.clipboardCopy(textToCopy: CharSequence) {
    simpleToast("$textToCopy copied")
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("RANDOM UUID", textToCopy)
    clipboard.setPrimaryClip(clip)
}

fun Context.clipboardPaste(paste: (String) -> Unit) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData: ClipData? = clipboard.primaryClip
    clipData?.apply { paste(getItemAt(0).toString().trim()) }
}

fun Context.simpleToast(text: CharSequence, duration: Int = Toast.LENGTH_LONG): Toast {
    val toast = Toast.makeText(this, text, duration)
    toast.show()
    return toast
}

fun Context.dynamicToast(text: CharSequence, delayMillis: Long = 500) {
    val toast = simpleToast(text, Toast.LENGTH_LONG)

    if (delayMillis < 3500) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ toast.cancel() }, delayMillis)
    }
}

fun Context.isConnect(onConnect: () -> Unit, onNotConnect: (String) -> Unit = {}): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return if (networkInfo != null && networkInfo.isConnected) {
        onConnect()
        true
    } else {
        onNotConnect(getString(R.string.error_internet_no_network))
        false
    }
}

fun Context.openChrome(s: String) {
    val i = Intent(Intent.ACTION_VIEW, Uri.parse(s))
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    i.setPackage("com.android.chrome")
    try {
        startActivity(i)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "unable to open chrome", Toast.LENGTH_SHORT).show()
        i.setPackage(null)
        startActivity(i)
    }
}

/**Example com.check.application*/
fun Context.otherAppIsExist(packageName: String): Boolean {
    val pm: PackageManager = packageManager
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.otherAppIntent(packageName: String) {
    val launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName)
    startActivity(launchIntentForPackage)
}

fun Context.openEmail(emailAddress: String, emailSubject: String = "", emailBodyMessage: String = "", emailAttachment: File? = null) {
    try {
        val emailIntent = Intent(Intent.ACTION_SENDTO)

        /*Type*/
        //emailIntent.type = "message/rfc822"
        //emailIntent.type = "text/plain"

        emailIntent.data = Uri.parse("mailto:") // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBodyMessage)

        /*Attachment*/
        emailAttachment?.let {
            /*val root = Environment.getExternalStorageDirectory()
            val pathToMyAttachedFile = "temp/attachement.xml"
            val file = File(root, pathToMyAttachedFile)*/
            val file = it
            if (!file.exists() || !file.canRead()) {
                simpleToast("Email Attachment Not exist / Can't read")
                return
            }
            val uri = Uri.fromFile(file)
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        }

        startActivity(Intent.createChooser(emailIntent, "Send Email"))
    } catch (e: ActivityNotFoundException) {
        simpleToast("There are no email client installed on your device.")
    }
}

/**
 * @param waFormat Example "https://api.whatsapp.com/send?phone=" + WA_PHONENUMBER + "&text=Example Text"
 * */
fun Context.openWhatsapp(waFormat: String, phoneNumber: String = "", message: String = "") {
    try {
        val intentAction = Intent(Intent.ACTION_VIEW)
        if (phoneNumber.isNotEmpty()) intentAction.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$message")
        else intentAction.data = Uri.parse(waFormat)
        startActivity(intentAction)
    } catch (e: ActivityNotFoundException) {
        simpleToast(getString(R.string.error_apps_not_found))
    }
}

fun Context.openWhatsappGroup(groupLink: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    val url = "https://chat.whatsapp.com/$groupLink"
    intent.data = Uri.parse(url)
    intent.setPackage("com.whatsapp")
    startActivity(intent)
}

fun Context.openCall(number: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$number")
    startActivity(intent)
}

fun Context.openLinkToWeb(url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(browserIntent)
}

fun Context.openContact(s: String) {
    val uri = Uri.parse(s)
    val likeIng = Intent(Intent.ACTION_VIEW, uri)

    try {
        startActivity(likeIng)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(s)
            )
        )
    }
}

fun Context.openSms(phone: String, text: String) {
    val sendIntent = Intent(Intent.ACTION_VIEW)
    sendIntent.data = Uri.parse("sms:")
    sendIntent.putExtra("address", phone)
    sendIntent.putExtra("sms_body", text)
    startActivity(sendIntent)
}

@Suppress("DEPRECATION")
fun Context.shareTextUrl(subject: String, message: String, title: String = "Share link!") {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

    // Add data to the intent, the receiving app will decide
    // what to do with it.
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, message)
    startActivity(Intent.createChooser(intent, title))
}

@Suppress("UnnecessaryVariable")
fun Context.shareImage(file: File, title: String = "Share Image!") {
    val intent = Intent(Intent.ACTION_SEND)

    // If you want to share a png image only, you can do:
    // setType("image/png"); OR for jpeg: setType("image/jpeg");
    intent.type = "image/*"

    // Make sure you put example png image named myImage.png in your
    // directory
    /*val imagePath = Environment.getExternalStorageDirectory().toString() + "/myImage.png"
    val imageFileToShare = File(imagePath)*/
    val imageFileToShare = file
    val uri = Uri.fromFile(imageFileToShare)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(Intent.createChooser(intent, title))
}

fun Context.shareMultipleImage(listOfUris: ArrayList<Uri>, comment: String?, title: String = "Share Image!") {
    if (listOfUris.isNotEmpty()) {
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, listOfUris)
            comment?.let { putExtra(Intent.EXTRA_TEXT, it) }

        }
        try {
            startActivity(Intent.createChooser(intent, title))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }
}

fun Context.convertUrlToUri(filesPath: ArrayList<String>): ArrayList<Uri> {
    val listOfUris = arrayListOf<Uri>()
    for (i in filesPath.indices) {
        Glide.with(this)
            .asBitmap()
            .load(filesPath[i])
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    resource.compress(Bitmap.CompressFormat.PNG, 100, ByteArrayOutputStream())
                    listOfUris.add(Uri.parse(MediaStore.Images.Media.insertImage(this@convertUrlToUri.contentResolver, resource, "", null)))
                }
            })
    }
    return listOfUris
}

@SuppressLint("ResourceType")
fun Context.getIntDimens(@IdRes idRes: Int): Int = resources.getDimension(idRes).toInt()

fun Context.doubleExit(doubleBackToExitPressedOnce: Boolean, listener: () -> Unit = {}): Boolean {
    var newDoubleBackToExitPressedOnce: Boolean
    if (doubleBackToExitPressedOnce) {
        listener.invoke()
        newDoubleBackToExitPressedOnce = false
    } else {
        simpleToast("Please click BACK again to exit", Toast.LENGTH_SHORT)
        Handler(Looper.getMainLooper()).postDelayed(Runnable { newDoubleBackToExitPressedOnce = false }, 2000)
        newDoubleBackToExitPressedOnce = true
    }

    return newDoubleBackToExitPressedOnce
}
