package com.kanzankazu.app.navigation

sealed class Screen(
    val route: String,
) {
    object Main : Screen("main")
    object Second : Screen("second")
}