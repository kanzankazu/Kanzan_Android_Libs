package com.kanzankazu.kanzanwidget.compose.util.internetconnectionobserver

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
}