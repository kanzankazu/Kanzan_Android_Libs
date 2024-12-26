@file:Suppress("unused")

package com.kanzankazu.kanzanbase

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.kanzankazu.kanzanutil.kanzanextension.simpleToast

abstract class BaseAppWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "ACTION_REFRESH"
    }

    abstract fun getLayout(): Int

    abstract fun onUpdateWidgetContent(context: Context, views: RemoteViews): RemoteViews

    open fun onUpdateWidgetViews(context: Context, layout: Int) = RemoteViews(context.packageName, layout)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(
                appWidgetId,
                onUpdateWidgetContent(context, onUpdateWidgetViews(context, getLayout()))
            )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            if (ACTION_REFRESH == intent.action) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisAppWidgetComponentName = ComponentName(
                    context.packageName,
                    javaClass.name
                )
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)

                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    fun refreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, javaClass).apply { action = ACTION_REFRESH }
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    /*inline fun <reified T> makePendingIntent(context: Context?): PendingIntent {
        val intent = Intent(context, T::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }
*/
    fun makeToast(context: Context, text: CharSequence, isShort: Boolean = true) {
        context.simpleToast(text, if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG)
    }
}
