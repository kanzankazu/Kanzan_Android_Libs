package com.kanzankazu.kanzanutil.kanzanextension

import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.onesignal.OneSignal

fun String.oneSignalSubscribeToTopic(isSubscribe: Boolean = true) {
    // Set the tag for the topic
    OneSignal.sendTag(this, if (isSubscribe) "true" else "false")

    // Log the subscription for verification
    this.debugMessageDebug("OneSignalExt - oneSignalSubscribeToTopic")
}