package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.kanzankazu.R

@Composable
fun SimpleFloatingActionButton(
    navHostControllerRoute: Pair<NavHostController, String>? = null,
    painterResourceId: Int? = null,
    imageVectorIcon: ImageVector? = null,
    contentDescription: String = "contentDescription",
    onclick: () -> Unit = {},
) {
    FloatingActionButton(
        shape = CircleShape,
        onClick = {
            onclick.invoke()
            navHostControllerRoute?.let {
                navHostControllerRoute.second.let {
                    navHostControllerRoute.first.navigate(it) {
                        popUpTo(navHostControllerRoute.first.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                Screen.Camera.route.let { navHostControllerRoute.first.navigate(it) }
            }
        },
        contentColor = Color.White
    ) {
        painterResourceId?.let {
            Image(
                painter = painterResource(id = painterResourceId),
                contentDescription = contentDescription
            )
        }
        imageVectorIcon?.let {
            Icon(
                imageVector = imageVectorIcon,
                contentDescription = "Add icon"
            )
        }
    }
}

@Preview()
@Composable
fun SimpleFloatingActionButtonPreview() {
    SimpleFloatingActionButton(painterResourceId = R.drawable.ic_android)
}