package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.twotone.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kanzankazu.R


sealed class Screen(val route: String, val title: String?, val icon: ImageVector?) {
    object PickUp : Screen("pickup", "PickUp", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Camera : Screen("camera", null, null)
}

val items = listOf(
    Screen.PickUp,
    Screen.Profile
)

@Preview()
@Composable
fun BottomBarWithFabDemPreview() {
    BottomBarWithFabDem()
}

@Composable
fun BottomBarWithFabDem() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            RoundedBottomAppBar(navController)
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            SimpleFloatingActionButton(
                navHostControllerRoute = Pair(navController, Screen.Camera.route),
                imageVectorIcon = Icons.TwoTone.Add
            )
        }
    ) {
        SampleMainScreenNavigation(navController)
    }
}

@Composable
fun RoundedBottomAppBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier
            .height(65.dp)
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
        cutoutShape = CircleShape,
        //backgroundColor = Color.White,
        elevation = 22.dp
    ) {
        BottomNav(navController = navController, screens = items)
    }
}

@Composable
fun BottomNav(navController: NavController, screens: List<Screen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination
    BottomNavigation(
        modifier = Modifier
            .padding(12.dp, 0.dp, 12.dp, 0.dp)
            .height(100.dp),
        //backgroundColor = Color.White,
        elevation = 0.dp
    ) {
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "",
                            modifier = Modifier.size(35.dp),
                            //tint = Color.Gray
                        )
                    }
                },
                label = {
                    screen.title?.let {
                        Text(
                            text = it,
                            //color = Color.Gray
                        )
                    }
                },
                selected = currentRoute?.hierarchy?.any { navDestination -> navDestination.route == navDestination.route } == true,
                selectedContentColor = Color(R.color.colorAccentItungItungan),
                unselectedContentColor = Color.White.copy(alpha = 0.4f),
                onClick = {
                    screen.route.let { s ->
                        navController.navigate(s) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SampleMainScreenNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Profile.route) {
        composable(Screen.Profile.route) {
        }
        composable(Screen.PickUp.route) {
        }
        composable(Screen.Camera.route) {
        }
    }
}