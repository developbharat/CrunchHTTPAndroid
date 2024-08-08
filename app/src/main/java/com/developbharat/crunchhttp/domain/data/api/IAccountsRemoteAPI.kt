package com.developbharat.crunchhttp.domain.data.api

import com.developbharat.crunchhttp.domain.data.api.accounts.SigninRequestData
import com.developbharat.crunchhttp.domain.data.api.accounts.UserAccountResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IAccountsRemoteAPI {
    @GET("accounts/whoami")
    suspend fun whoami(): UserAccountResponse

    @POST("accounts/signin")
    suspend fun signin(@Body() data: SigninRequestData): UserAccountResponse
}