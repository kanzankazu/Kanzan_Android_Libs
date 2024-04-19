package com.kanzankazu.kanzanbase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kanzankazu.kanzanmodel.MyFirebaseNotificationModel
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage
import com.kanzankazu.kanzanutil.kanzanextension.type.json2Object
import org.json.JSONObject

abstract class BaseMessagingService : FirebaseMessagingService() {
    val allUserTopic = "AllUserApp"
    val adminChannelId = "admin_channel"
    lateinit var notif: MyFirebaseNotificationModel

    abstract fun onMessageReceivedListener(
        notif: MyFirebaseNotificationModel,
        remoteMessage: RemoteMessage,
    )

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        "onMessageReceived BaseMessagingService $remoteMessage".debugMessage()
        "onMessageReceived BaseMessagingService ${remoteMessage.from}".debugMessage()

        if (remoteMessage.data.isNotEmpty()) {
            val map = remoteMessage.data
            val mapToObject = JSONObject(map as Map<*, *>)
            "onMessageReceived BaseMessagingService $mapToObject".debugMessage()
            notif = mapToObject.toString().json2Object(MyFirebaseNotificationModel::class.java)
            "onMessageReceived BaseMessagingService $notif".debugMessage()
            "onMessageReceived BaseMessagingService isNotEmpty".debugMessage()
        } else {
            "onMessageReceived BaseMessagingService isEmpty".debugMessage()
        }

        onMessageReceivedListener(notif, remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        "onNewToken BaseMessagingService $token".debugMessage()
    }
}
