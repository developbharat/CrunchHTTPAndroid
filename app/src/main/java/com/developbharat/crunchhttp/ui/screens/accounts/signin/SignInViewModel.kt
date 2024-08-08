package com.developbharat.crunchhttp.ui.screens.accounts.signin

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developbharat.crunchhttp.domain.uses.accounts.IsAuthenticatedUseCase
import com.developbharat.crunchhttp.domain.uses.accounts.SignInUserAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUserAccountUseCase: SignInUserAccountUseCase,
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase
) : ViewModel() {
    private val _state = mutableStateOf(SigninState())
    val state: State<SigninState> = _state


    init {
        _state.value = _state.value.copy(authenticatedAccount = isAuthenticatedUseCase())
    }

    fun setMobile(mobile: String) {
        _state.value = _state.value.copy(mobile = mobile)
    }

    @SuppressLint("HardwareIds")
    fun startSignin(context: Context) {
        val contentResolver = context.contentResolver
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        signInUserAccountUseCase(_state.value.mobile, androidId).onEach {
            _state.value = _state.value.copy(status = it.status)
        }.launchIn(viewModelScope)
    }
}