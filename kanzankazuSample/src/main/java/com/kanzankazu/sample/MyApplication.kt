package com.kanzankazu.sample

import androidx.multidex.MultiDexApplication

class MyApplication : MultiDexApplication() {
    companion object {
        lateinit var instance: MyApplication

        fun getApp(): MyApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}