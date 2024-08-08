package com.developbharat.crunchhttp.ui.screens.accounts.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.developbharat.crunchhttp.R
import com.developbharat.crunchhttp.ui.components.ActionInProgressView


@OptIn(ExperimentalMaterial3Api::class)
@Composable()
fun SigninScreen(viewModel: SignInViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Welcome") }) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            if (state.authenticatedAccount == null) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Crunch HTTP",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (state.status.isInProgress) {
                    ActionInProgressView(status = state.status)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                OutlinedTextField(
                    label = { Text("Mobile") },
                    value = state.mobile,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    onValueChange = { viewModel.setMobile(it) },
                    isError = state.status.isError,
                    supportingText = {
                        if (state.status.isError) {
                            Text(state.status.statusText)
                        }
                    },
                    enabled = !state.status.isInProgress,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = { viewModel.startSignin(context) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Start login")
                }
            }


            if (state.authenticatedAccount != null) {
                Text("You are authenticated, You can close the app now. We will keep working in background.")
            }
        }
    }
}