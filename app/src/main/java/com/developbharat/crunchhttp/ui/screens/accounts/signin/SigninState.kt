package com.developbharat.crunchhttp.ui.screens.accounts.signin

import com.developbharat.crunchhttp.common.ResourceStatus
import com.developbharat.crunchhttp.domain.models.accounts.UserAccount

data class SigninState(
    val mobile: String = "",
    val status: ResourceStatus = ResourceStatus(),
    val authenticatedAccount: UserAccount? = null
)