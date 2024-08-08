package com.developbharat.crunchhttp.domain.repos.accounts

import com.developbharat.crunchhttp.domain.models.accounts.UserAccount

interface IAccountsRepository {
    suspend fun whoami(): UserAccount
    suspend fun signin(mobile: String, deviceId: String): UserAccount
    fun isAuthenticated(): UserAccount?
}