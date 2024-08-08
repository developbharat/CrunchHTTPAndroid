package com.developbharat.crunchhttp.domain.data.api.accounts

import com.developbharat.crunchhttp.domain.data.api.accounts.UserAccountResData
import com.google.gson.annotations.SerializedName

data class UserAccountResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: UserAccountResData?
)

