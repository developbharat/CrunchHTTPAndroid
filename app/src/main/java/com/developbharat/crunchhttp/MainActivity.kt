package com.developbharat.crunchhttp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.developbharat.crunchhttp.common.Routes
import com.developbharat.crunchhttp.ui.screens.accounts.signin.SigninScreen
import com.developbharat.crunchhttp.ui.theme.CrunchHTTPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrunchHTTPTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Routes.SigninScreen) {
                    // Start and Home Screens
                    composable<Routes.SigninScreen> {
                        SigninScreen()
                    }
                }
            }
        }
    }
}