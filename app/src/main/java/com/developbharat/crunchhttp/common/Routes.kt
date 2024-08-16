package com.developbharat.crunchhttp.common

import kotlinx.serialization.Serializable


@Serializable
sealed interface Routes {
    @Serializable
    data object StartScreen : Routes
}