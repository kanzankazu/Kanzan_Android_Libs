// SecondScreen.kt
package com.kanzankazu.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(
    id: String? = null,
    name: String? = null,
    forResult: Boolean = false,
    userData: UserData? = null,
    jsonData: String? = null,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Second Screen") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (id != null) {
                Text("ID: $id")
            }
            if (name != null) {
                Text("Name: $name")
            }
            if (forResult) {
                Text("This screen was opened for result")
            }
            userData?.let { data ->
                Text("User Data:")
                Text("- ID: ${data.id}")
                Text("- Name: ${data.name}")
                Text("- Email: ${data.email}")
            }
            jsonData?.let {
                Text("JSON Data:")
                Text(it)
            }
        }
    }
}

@Preview
@Composable
fun SecondScreenPreview() {
    SecondScreen(
        id = "123",
        name = "Test User",
        userData = UserData("123", "Test User", "test@example.com"),
        jsonData = "{\"id\":\"123\",\"name\":\"Test User\"}",
        onBack = {}
    )
}