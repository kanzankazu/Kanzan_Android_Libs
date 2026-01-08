package com.kanzankazu.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KanzanApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
