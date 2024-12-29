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

    abstract fun onMessageReceivedListener(
        notif: MyFirebaseNotificationModel,
        remoteMessage: RemoteMessage,
    )

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        "onMessageReceived BaseMessagingService $remoteMessage".debugMessageDebug()
        "onMessageReceived BaseMessagingService ${remoteMessage.from}".debugMessageDebug()

        if (remoteMessage.data.isNotEmpty()) {
            val map = remoteMessage.data
            val mapToObject = JSONObject(map as Map<*, *>)
            "onMessageReceived BaseMessagingService $mapToObject".debugMessageDebug()
            notif = mapToObject.toString().json2Object(MyFirebaseNotificationModel::class.java)
            "onMessageReceived BaseMessagingService $notif".debugMessageDebug()
            "onMessageReceived BaseMessagingService isNotEmpty".debugMessageDebug()
        } else {
            "onMessageReceived BaseMessagingService isEmpty".debugMessageDebug()
        }

        onMessageReceivedListener(notif, remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        "onNewToken BaseMessagingService $token".debugMessageDebug()
    }
}
