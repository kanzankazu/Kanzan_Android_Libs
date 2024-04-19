package com.kanzankazu.kanzanbase.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

abstract class BaseActivityCompose : ComponentActivity() {

    @Composable
    abstract fun SetContentCompose()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetContentCompose()
        }
    }
}