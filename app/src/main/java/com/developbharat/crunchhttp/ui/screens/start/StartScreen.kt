package com.developbharat.crunchhttp.ui.screens.start

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(viewModel: StartViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val context = LocalContext.current

    // check if device is authenticated on initial load
    LaunchedEffect(Unit) {
        viewModel.checkIsDeviceAuthenticated()
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
                Text("Device ID: " + viewModel.checkDeviceId(context))
                Text("You need to add your device in admin panel to use it for crunching http requests.")
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { viewModel.checkIsDeviceAuthenticated() }) { Text("Recheck Authentication") }
            }
        }
    }
}