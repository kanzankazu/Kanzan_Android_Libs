package com.kanzankazu.kanzanbase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kanzankazu.kanzanmodel.KanzanFirebaseNotificationModel
import com.kanzankazu.kanzanutil.enums.NotifTopics
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import com.kanzankazu.kanzanutil.kanzanextension.type.json2Object
import org.json.JSONObject

abstract class BaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val ADMIN_CHANNEL_ID = "admin_channel"
    }

    private lateinit var notif: KanzanFirebaseNotificationModel

    abstract fun onMessageReceivedListener(
        notif: KanzanFirebaseNotificationModel,
        remoteMessage: RemoteMessage,
    )

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        "onMessageReceived: $remoteMessage".debugMessageDebug("BaseMessagingService - onMessageReceived")

        // Default inisialisasi
        notif = KanzanFirebaseNotificationModel()

        if (remoteMessage.data.isNotEmpty()) {
            try {
                val map = remoteMessage.data
                val mapToObject = JSONObject(map as Map<*, *>)
                notif = mapToObject.toString().json2Object(KanzanFirebaseNotificationModel::class.java)
            } catch (e: Exception) {
                e.debugMessageError("BaseMessagingService - onMessageReceived")
                "Error parsing notification data: ${e.message}".debugMessageDebug("BaseMessagingService - onMessageReceived")
            }
        } else {
            "RemoteMessage data is empty".debugMessageDebug("BaseMessagingService - onMessageReceived")
        }

        onMessageReceivedListener(notif, remoteMessage)
    }

    override fun onNewToken(token: String) {
        "onNewToken received: $token".debugMessageDebug("BaseMessagingService - onNewToken")
    }
}