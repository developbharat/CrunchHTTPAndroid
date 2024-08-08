package com.developbharat.crunchhttp.common

import kotlinx.serialization.Serializable


@Serializable
sealed interface Routes {
    // Authentication Screens
    @Serializable
    data object SigninScreen : Routes
}