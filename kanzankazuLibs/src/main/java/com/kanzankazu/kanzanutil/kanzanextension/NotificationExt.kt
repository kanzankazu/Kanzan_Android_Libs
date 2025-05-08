@file:Suppress("SameParameterValue", "DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug

fun setNotification(context: Context, title: String?, text: String?, smallIcon: Int, largerIcon: Int, isNotCancelAble: Boolean, pendingIntent: PendingIntent?, NOTIFICATION_ID: Int) {
    val bitmap = BitmapFactory.decodeResource(context.resources, largerIcon)
    setNotification(context, title, text, smallIcon, bitmap, isNotCancelAble, pendingIntent, NOTIFICATION_ID)
}

fun setNotification(context: Context, title: String?, text: String?, smallIcon: Int, largeIcon: Bitmap?, isNotCancelAble: Boolean, pendingIntent: PendingIntent?, NOTIFICATION_ID: Int) {
    val notificationBuilder = Notification.Builder(context)
    notificationBuilder.setContentTitle(title)
    notificationBuilder.setContentText(text)
    if (pendingIntent != null) {
        notificationBuilder.setContentIntent(pendingIntent)
    }
    //notificationBuilder.setSmallIcon(Icon.createWithBitmap(PictureUtil.createBitmapFromString("10", " KB")));
    notificationBuilder.setSmallIcon(smallIcon)
    notificationBuilder.setLargeIcon(largeIcon)
    notificationBuilder.setAutoCancel(true)
    notificationBuilder.setOngoing(isNotCancelAble)
    notificationBuilder.setPriority(Notification.PRIORITY_MAX)
    //notificationBuilder.setProgress();
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}

@RequiresApi(api = Build.VERSION_CODES.M)
fun setPingNotif(context: Context, title: String?, text: String?, pingSpeed: String, isNotCancelAble: Boolean, pendingIntent: PendingIntent?, NOTIFICATION_ID: Int) {
    val notificationBuilder = Notification.Builder(context)
    notificationBuilder.setContentTitle(title)
    notificationBuilder.setContentText(text)
    if (pendingIntent != null) {
        notificationBuilder.setContentIntent(pendingIntent)
    }
    notificationBuilder.setSmallIcon(Icon.createWithBitmap(createBitmapFromString(pingSpeed, "ms")))
    notificationBuilder.setAutoCancel(true)
    notificationBuilder.setOngoing(isNotCancelAble)
    notificationBuilder.setPriority(Notification.PRIORITY_MAX)
    //notificationBuilder.setProgress();
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}

fun createBitmapFromString(speed: String, units: String): Bitmap {
    val speedPaint = Paint()
    speedPaint.isFakeBoldText = true
    speedPaint.textSize = 55f
    speedPaint.textAlign = Paint.Align.CENTER
    val unitsPaint = Paint()
    unitsPaint.isFakeBoldText = true
    unitsPaint.isAntiAlias = true
    unitsPaint.textSize = 40f // size is in pixels
    unitsPaint.textAlign = Paint.Align.CENTER
    val textBounds = Rect()
    speedPaint.getTextBounds(speed, 0, speed.length, textBounds)
    val unitsTextBounds = Rect()
    unitsPaint.getTextBounds(units, 0, units.length, unitsTextBounds)
    val width = if (textBounds.width() > unitsTextBounds.width()) textBounds.width() else unitsTextBounds.width()
    val bitmap = Bitmap.createBitmap(width + 10, 90, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawText(speed, (width / 2 + 5).toFloat(), 50f, speedPaint)
    canvas.drawText(units, (width / 2).toFloat(), 90f, unitsPaint)
    "createBitmapFromString PictureUtil textBounds : ${textBounds.width()}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil textBounds : ${textBounds.height()}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil unitsTextBounds : ${unitsTextBounds.width()}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil unitsTextBounds : ${unitsTextBounds.height()}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil bitmap.getHeight : ${bitmap.height}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil bitmap.getWidth : ${bitmap.width}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil canvas.getHeight: ${canvas.height}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil canvas.getWidth: ${canvas.width}".debugMessageDebug(" - createBitmapFromString")
    "createBitmapFromString PictureUtil width : $width".debugMessageDebug(" - createBitmapFromString")
    return bitmap
}

@SuppressLint("RemoteViewLayout")
fun setCustomNotification(context: Context, title: String?, text: String?, smallIcon: Int, bigIcon: Bitmap?, isNotCancelAble: Boolean, pendingIntent: PendingIntent?, NOTIFICATION_ID: Int) {
    val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val mRemoteViews = RemoteViews(context.packageName, R.layout.layout_notification_small)
    mRemoteViews.setImageViewBitmap(R.id.notif_icon, bigIcon)
    mRemoteViews.setTextViewText(R.id.notif_title, title)
    mRemoteViews.setTextViewText(R.id.notif_content, text)
    val apiVersion = Build.VERSION.SDK_INT
    if (apiVersion < Build.VERSION_CODES.HONEYCOMB) {
        val mNotification = Notification(smallIcon, title, System.currentTimeMillis())
        mNotification.contentView = mRemoteViews
        mNotification.defaults = mNotification.defaults or Notification.DEFAULT_LIGHTS
        //mNotification.contentIntent = pendingIntent;
        //mNotification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification)
    } else {
        val mBuilder = Notification.Builder(context)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(smallIcon)
            .setContent(mRemoteViews) //.setAutoCancel(isNotCancelAble) //Do not clear the notification
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }
}

@RequiresApi(api = Build.VERSION_CODES.M)
fun isNotificationShow1(context: Context, NOTIFICATION_ID: Int): Boolean {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notifications = notificationManager.activeNotifications
    for (notification in notifications) {
        return notification.id == NOTIFICATION_ID
    }
    return false
}

private fun isNotificationShow2(context: Context, NOTIFICATION_ID: Int): Boolean {
    val notificationIntent = Intent(context, context.javaClass)
    val test = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE)
    return test != null
}

fun clearNotification(context: Context, NOTIFICATION_ID: Int) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(NOTIFICATION_ID)
}

fun clearNotificationAll(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancelAll()
}