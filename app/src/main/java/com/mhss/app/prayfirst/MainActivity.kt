package com.mhss.app.prayfirst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mhss.app.prayfirst.presentation.Screen
import com.mhss.app.prayfirst.presentation.main.MainScreen
import com.mhss.app.prayfirst.presentation.settings.SettingsScreen
import com.mhss.app.prayfirst.ui.theme.PrayFirstTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PrayFirstTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val snackBarHostState = remember { SnackbarHostState() }
                    val scope = rememberCoroutineScope()
                    Scaffold(
                        bottomBar = {
                            val screens = listOf(
                                Screen.Main,
                                Screen.Settings
                            )
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ) {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination
                                screens.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(painterResource(
                                            if (currentDestination?.hierarchy?.any { it.route == screen.route } == true)
                                                screen.iconSelectedres
                                            else
                                                screen.iconRes
                                        ), contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                            )
                                               },
                                        label = { Text(stringResource(screen.titleRes)) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                                        )
                                    )
                                }
                            }
                        },
                        snackbarHost = { SnackbarHost(snackBarHostState) },
                    ) { innerPadding ->
                        NavHost(navController, startDestination = Screen.Main.route, Modifier.padding(innerPadding)) {
                            composable(Screen.Main.route) { MainScreen {
                                scope.launch {
                                    snackBarHostState.showSnackbar(it)
                                }
                            } }
                            composable(Screen.Settings.route) { SettingsScreen() }
                        }
                    }
                }
            }
        }
    }
}