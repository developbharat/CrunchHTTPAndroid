package com.developbharat.crunchhttp.common

data class ResourceStatus(
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val isInProgress: Boolean = false,
    val statusText: String = "",
)