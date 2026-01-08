// Di file NavigationEvent.kt
package com.kanzankazu.app.navigation

import com.kanzankazu.app.ui.screen.UserData

sealed class NavigationEvent {
    // Basic Navigation
    object NavigateToSecond : NavigationEvent()
    data class NavigateWithData(val id: String, val name: String) : NavigationEvent()
    
    // UI Components
    data class ShowDialog(
        val title: String,
        val message: String,
        val confirmText: String = "OK",
        val dismissText: String = "Cancel",
        val onConfirm: (() -> Unit)? = null,
        val onDismiss: () -> Unit = {}
    ) : NavigationEvent()
    object ShowBottomSheet : NavigationEvent()
    
    // Advanced Navigation
    data class NavigateWithDeepLink(val deepLink: String) : NavigationEvent()
    object NavigateForResult : NavigationEvent()
    data class NavigateWithModel(val userData: UserData) : NavigationEvent()
    data class NavigateWithJson(val jsonString: String) : NavigationEvent()
    
    // Navigation Actions
    object NavigateBack : NavigationEvent()
    data class NavigateToRoute(val route: String) : NavigationEvent()
}