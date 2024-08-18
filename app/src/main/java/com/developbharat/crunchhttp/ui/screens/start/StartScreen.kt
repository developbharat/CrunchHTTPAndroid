package com.developbharat.crunchhttp.ui.screens.start

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.developbharat.crunchhttp.services.HttpTaskWorker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    viewModel: StartViewModel = hiltViewModel(),
    appContext: Context
) {
    val state = viewModel.state.value

    // check if device is authenticated on initial load
    LaunchedEffect(Unit) {
        viewModel.checkIsDeviceAuthenticated()
    }

    LaunchedEffect(state.clientDevice) {
        // Don't schedule anything if device is authenticated.
        if (state.clientDevice == null) return@LaunchedEffect

        HttpTaskWorker.schedulePeriodicTask(appContext)
        HttpTaskWorker.scheduleOneTimeTask(appContext)
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Welcome") }) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            if (state.isInProgress) {
                Text("Checking Is This device authenticated...")
            }

            if (state.clientDevice != null) {
                Text("You are authenticated. We will keep working in background. You can close this app now.")
            }

            if (state.clientDevice == null) {
                Text("Device ID: " + viewModel.checkDeviceId(appContext))
                Text("You need to add your device in admin panel to use it for crunching http requests.")
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { viewModel.checkIsDeviceAuthenticated() }) { Text("Recheck Authentication") }
            }
        }
    }
}