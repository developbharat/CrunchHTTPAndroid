package com.developbharat.crunchhttp.domain.data.api.accounts

import com.google.gson.annotations.SerializedName

data class SigninRequestData(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("device_id") val deviceId: String,
)
