package com.kanzankazu.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.kanzankazu.app.navigation.NavigationEvent
import com.kanzankazu.app.ui.component.FillMaxWidthButton
import com.kanzankazu.app.ui.theme.KanzanBaseTheme

// Data class untuk contoh navigasi dengan model
data class UserData(
    val id: String,
    val name: String,
    val email: String,
)

@Composable
fun MainScreen(
    onNavigationEvent: (NavigationEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Navigasi Sederhana
        FillMaxWidthButton(
            title = "1. Navigasi ke Second Screen"
        ) {
            onNavigationEvent(NavigationEvent.NavigateToSecond)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 2. Navigasi dengan Data
        FillMaxWidthButton(
            title = "2. Navigasi dengan Data"
        ) {
            onNavigationEvent(NavigationEvent.NavigateWithData("user123", "Kanzan"))
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 3. Tampilkan Dialog
        FillMaxWidthButton(
            "3. Tampilkan Dialog"
        ) {
            onNavigationEvent(
                NavigationEvent.ShowDialog(
                    title = "Konfirmasi",
                    message = "Apakah Anda yakin ingin menampilkan dialog ini?",
                    confirmText = "Ya",
                    dismissText = "Tidak",
                    onConfirm = { println("Dialog dikonfirmasi") },
                    onDismiss = { println("Dialog ditutup") }
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 4. Tampilkan Bottom Sheet
        FillMaxWidthButton(
            "4. Tampilkan Bottom Sheet"
        ) {
            onNavigationEvent(NavigationEvent.ShowBottomSheet)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 5. Navigasi dengan Deep Link
        FillMaxWidthButton(
            "5. Navigasi dengan Deep Link"
        ) {
            onNavigationEvent(NavigationEvent.NavigateWithDeepLink("kanzanapp://profile/user123?source=deeplink"))
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 6. Navigasi dengan Return Value
        FillMaxWidthButton(
            "6. Navigasi dengan Return Value"
        ) {
            onNavigationEvent(NavigationEvent.NavigateForResult)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 7. Navigasi dengan Data Model
        FillMaxWidthButton(
            "7. Navigasi dengan Data Model"
        ) {
            val userData = UserData(
                id = "user123",
                name = "Kanzan",
                email = "kanzan@example.com"
            )
            onNavigationEvent(NavigationEvent.NavigateWithModel(userData))
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 8. Navigasi dengan Data JSON
        FillMaxWidthButton(
            "8. Navigasi dengan Data JSON"
        ) {
            val userData = UserData(
                id = "user123",
                name = "Kanzan",
                email = "kanzan@example.com"
            )
            val jsonString = Gson().toJson(userData)
            onNavigationEvent(NavigationEvent.NavigateWithJson(jsonString))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    KanzanBaseTheme {
        MainScreen(
            onNavigationEvent = { event ->
                // Handle navigation events in preview
                when (event) {
                    is NavigationEvent.NavigateToSecond -> println("Navigating to second screen")
                    is NavigationEvent.NavigateWithData -> println("Navigating with data: ${event.id}, ${event.name}")
                    is NavigationEvent.ShowDialog -> println("Showing dialog")
                    is NavigationEvent.ShowBottomSheet -> println("Showing bottom sheet")
                    is NavigationEvent.NavigateWithDeepLink -> println("Navigating with deep link: ${event.deepLink}")
                    is NavigationEvent.NavigateForResult -> println("Navigating for result")
                    is NavigationEvent.NavigateWithModel -> println("Navigating with model: ${event.userData}")
                    is NavigationEvent.NavigateWithJson -> println("Navigating with JSON: ${event.jsonString}")
                    is NavigationEvent.NavigateBack -> println("Navigating back")
                    is NavigationEvent.NavigateToRoute -> println("Navigating to route: ${event.route}")
                }
            }
        )
    }
}