package com.developbharat.crunchhttp.ui.screens.start

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.developbharat.crunchhttp.IsDeviceAuthenticatedQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val apolloClient: ApolloClient
) : ViewModel() {
    private val _state = mutableStateOf(StartState())
    val state: State<StartState> = _state


    fun checkIsDeviceAuthenticated() {
        // Set current state to loading
        _state.value = _state.value.copy(isInProgress = true)

        // Make GraphQL Request to server
        viewModelScope.launch {
            val result = apolloClient.query(IsDeviceAuthenticatedQuery()).execute()
            if (!result.hasErrors() && result.data !== null && result.data!!.isDeviceAuthenticated !== null) {
                _state.value = _state.value.copy(
                    clientDevice = result.data!!.isDeviceAuthenticated!!.clientDevice,
                    isInProgress = false
                )
            } else {
                // Remove session if persisted if device is unauthenticated and set clientDevice to null
                _state.value = _state.value.copy(clientDevice = null, isInProgress = false)
            }
        }
    }


    @SuppressLint("HardwareIds")
    fun checkDeviceId(context: Context): String {
        val contentResolver = context.contentResolver
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("deviceId", androidId)
        return androidId
    }
}