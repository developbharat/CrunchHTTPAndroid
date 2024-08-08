package com.developbharat.crunchhttp.domain.data.api.accounts

import com.developbharat.crunchhttp.domain.models.accounts.UserAccount
import com.google.gson.annotations.SerializedName

data class UserAccountResData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("auth_token") val authToken: String,
    @SerializedName("account_activated_at") val accountActivatedAt: Long? = null
) {
    fun toUserAccount(): UserAccount {
        return UserAccount(
            id = this.id,
            name = this.name,
            mobile = this.mobile,
            authToken = this.authToken,
            accountActivatedAt = this.accountActivatedAt,
        )
    }
}