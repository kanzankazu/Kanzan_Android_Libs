@file:Suppress("unused")

package com.kanzankazu.kanzanbase

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError

abstract class BaseAppWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_REFRESH = "com.kanzankazu.kanzanbase.ACTION_REFRESH"
    }

    // Template method pattern: subclass wajib mengimplementasikan ini
    abstract fun getLayout(): Int
    abstract fun onUpdateWidgetContent(context: Context, views: RemoteViews): RemoteViews

    // Fungsi pembantu untuk menghasilkan RemoteViews dari layout
    open fun onUpdateWidgetViews(context: Context, layout: Int): RemoteViews =
        RemoteViews(context.packageName, layout)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            try {
                val updatedViews = onUpdateWidgetContent(
                    context,
                    onUpdateWidgetViews(context, getLayout())
                )
                appWidgetManager.updateAppWidget(appWidgetId, updatedViews)
            } catch (e: Exception) {
                e.printStackTrace().debugMessageError("BaseAppWidgetProvider - onUpdate")
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null || intent.action.isNullOrEmpty()) {
            return // Invalid Intent, abaikan
        }
        when (intent.action) {
            ACTION_REFRESH -> handleRefresh(context)
            else -> super.onReceive(context, intent) // Delegate ke default handler
        }
    }

    private fun handleRefresh(context: Context) {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
        } catch (e: Exception) {
            e.printStackTrace().debugMessageError("BaseAppWidgetProvider - handleRefresh")
        }
    }

    fun refreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, javaClass).apply {
            action = ACTION_REFRESH
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // Menghindari deprecation warning
        )
    }
}