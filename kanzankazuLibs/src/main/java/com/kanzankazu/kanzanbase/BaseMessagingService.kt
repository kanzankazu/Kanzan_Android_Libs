package com.kanzankazu.kanzanbase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kanzankazu.kanzanmodel.MyFirebaseNotificationModel
import com.kanzankazu.kanzanutil.NotifTopics
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.json2Object
import org.json.JSONObject

abstract class BaseMessagingService : FirebaseMessagingService() {
    val allUserTopic = NotifTopics.ALL_USER_APP.topic
    val adminChannelId = "admin_channel"
    lateinit var notif: MyFirebaseNotificationModel
        private set

    abstract fun onMessageReceivedListener(
        notif: MyFirebaseNotificationModel,
        remoteMessage: RemoteMessage,
    )

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        "onMessageReceived: $remoteMessage".debugMessageDebug()

        // Default inisialisasi
        notif = MyFirebaseNotificationModel()

        if (remoteMessage.data.isNotEmpty()) {
            try {
                val map = remoteMessage.data
                val mapToObject = JSONObject(map as Map<*, *>)
                notif = mapToObject.toString().json2Object(MyFirebaseNotificationModel::class.java)
            } catch (e: Exception) {
                "Error parsing notification data: ${e.message}".debugMessageDebug()
            }
        } else {
            "RemoteMessage data is empty".debugMessageDebug()
        }

        onMessageReceivedListener(notif, remoteMessage)
    }

    override fun onNewToken(token: String) {
        "onNewToken received: $token".debugMessageDebug()
    }
}