package com.kanzankazu.kanzanwidget.compose.extension

import androidx.navigation.NavController

/** Navigate dengan launchSingleTop = true */
fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

/** Navigate dengan popUpTo untuk clear back stack */
fun NavController.navigateAndClearBackStack(
    route: String,
    popUpToRoute: String,
    inclusive: Boolean = false,
) {
    navigate(route) {
        popUpTo(popUpToRoute) {
            this.inclusive = inclusive
        }
        launchSingleTop = true
    }
}

/** Navigate sebagai tab — save/restore state, popUpTo root */
fun NavController.navigateAsTab(
    route: String,
    rootRoute: String,
) {
    navigate(route) {
        popUpTo(rootRoute) {
            saveState = true
        }
        restoreState = true
        launchSingleTop = true
    }
}
