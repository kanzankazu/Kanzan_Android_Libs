// AppNavigation.kt
package com.kanzankazu.app.navigation

import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.kanzankazu.app.ui.screen.MainScreen
import com.kanzankazu.app.ui.screen.SecondScreen
import com.kanzankazu.app.ui.screen.UserData

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    var dialogEvent by remember { mutableStateOf<NavigationEvent.ShowDialog?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        // Main Screen
        composable(Screen.Main.route) {
            MainScreen { event ->
                when (event) {
                    is NavigationEvent.NavigateToSecond -> navController.navigate(Screen.Second.route)
                    is NavigationEvent.NavigateWithData -> navController.navigate("${Screen.Second.route}?id=${event.id}&name=${event.name}")
                    is NavigationEvent.ShowDialog -> dialogEvent = event
                    is NavigationEvent.ShowBottomSheet -> {}
                    is NavigationEvent.NavigateWithDeepLink -> navController.navigate(event.deepLink)
                    is NavigationEvent.NavigateForResult -> navController.navigate("${Screen.Second.route}?forResult=true")
                    is NavigationEvent.NavigateWithModel -> {
                        val json = Uri.encode(Gson().toJson(event.userData))
                        navController.navigate("${Screen.Second.route}?userData=$json")
                    }

                    is NavigationEvent.NavigateWithJson -> navController.navigate("${Screen.Second.route}?jsonData=${event.jsonString}")
                    is NavigationEvent.NavigateBack -> navController.popBackStack()
                    is NavigationEvent.NavigateToRoute -> navController.navigate(event.route) { popUpTo(0) }
                }
            }
        }

        // Second Screen
        composable(
            route = "${Screen.Second.route}?id={id}&name={name}&forResult={forResult}&userData={userData}&jsonData={jsonData}",
            arguments = listOf(
                navArgument("id") {
                    nullable = true
                    defaultValue = null
                },
                navArgument("name") {
                    nullable = true
                    defaultValue = null
                },
                navArgument("forResult") {
                    nullable = true
                    defaultValue = false
                },
                navArgument("userData") {
                    nullable = true
                    defaultValue = null
                },
                navArgument("jsonData") {
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val name = backStackEntry.arguments?.getString("name")
            val forResult = backStackEntry.arguments?.getBoolean("forResult") ?: false
            val userDataJson = backStackEntry.arguments?.getString("userData")
            val jsonData = backStackEntry.arguments?.getString("jsonData")

            val userData = userDataJson?.let {
                Gson().fromJson(Uri.decode(it), UserData::class.java)
            }

            SecondScreen(
                id = id,
                name = name,
                forResult = forResult,
                userData = userData,
                jsonData = jsonData,
                onBack = { navController.popBackStack() }
            )
        }
    }

    dialogEvent?.let { dlg ->
        AlertDialog(
            onDismissRequest = {
                dlg.onDismiss()
                dialogEvent = null
            },
            title = { Text(text = dlg.title) },
            text = { Text(text = dlg.message) },
            confirmButton = {
                if (dlg.onConfirm != null) {
                    TextButton(onClick = {
                        dlg.onConfirm.invoke()
                        dialogEvent = null
                    }) { Text(dlg.confirmText) }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dlg.onDismiss()
                    dialogEvent = null
                }) { Text(dlg.dismissText) }
            }
        )
    }
}