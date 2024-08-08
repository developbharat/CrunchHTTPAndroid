package com.developbharat.crunchhttp.domain.repos.accounts

import com.developbharat.crunchhttp.common.SharedStoreKeys
import com.developbharat.crunchhttp.domain.data.api.IAccountsRemoteAPI
import com.developbharat.crunchhttp.domain.data.api.accounts.SigninRequestData
import com.developbharat.crunchhttp.domain.models.accounts.UserAccount
import com.developbharat.crunchhttp.domain.store.ISharedStore
import java.time.Instant
import javax.inject.Inject

class AccountsRepository @Inject constructor(
    private val api: IAccountsRemoteAPI,
    private val sharedStore: ISharedStore,
) : IAccountsRepository {
    override suspend fun whoami(): UserAccount {
        // resolve from cache is user is authenticated
        if (this.isUserAccountCached())
            return sharedStore.useValue(SharedStoreKeys.USER_ACCOUNT, UserAccount::class.java)

        // otherwise resolve user account from internet
        val response = api.whoami()

        // Raise exception and Remove current user account from cache in case of failure
        if (response.data === null || !response.success) {
            this.deleteUserAccountCache()
            throw Exception(response.status)
        }

        // store user account in cache
        val account = response.data.toUserAccount()
        this.cacheUserAccount(account = account)

        return account
    }

    override suspend fun signin(mobile: String, deviceId: String): UserAccount {
        val response = api.signin(SigninRequestData(mobile = mobile, deviceId = deviceId))
        if (response.data === null || !response.success) throw Exception(response.status)

        // store user account in cache
        val account = response.data.toUserAccount()
        this.cacheUserAccount(account)

        return account
    }

    override fun isAuthenticated(): UserAccount? {
        // resolve from cache is user is authenticated
        if (this.isUserAccountCached())
            return sharedStore.useValue(SharedStoreKeys.USER_ACCOUNT, UserAccount::class.java)

        return null;
    }

    private fun cacheUserAccount(account: UserAccount) {
        // increase cache expire time for 12 Hours
        val expiresIn = Instant.now().plusSeconds(60 * 60 * 12).toEpochMilli()
        sharedStore.setValue(SharedStoreKeys.USER_ACCOUNT_CACHED_TILL, expiresIn)

        // store account in cache
        sharedStore.setValue(SharedStoreKeys.USER_ACCOUNT, account)
    }

    private fun isUserAccountCached(): Boolean {
        // serve from cache if session not yet expired.
        if (sharedStore.isValueAvailable(SharedStoreKeys.USER_ACCOUNT_CACHED_TILL) &&
            sharedStore.isValueAvailable(SharedStoreKeys.USER_ACCOUNT) &&
            sharedStore.useValue(SharedStoreKeys.USER_ACCOUNT_CACHED_TILL, Long::class.java) > Instant.now()
                .toEpochMilli()
        ) {
            return true
        }

        return false
    }

    private fun deleteUserAccountCache() {
        sharedStore.deleteValue(SharedStoreKeys.USER_ACCOUNT)
        sharedStore.deleteValue(SharedStoreKeys.USER_ACCOUNT_CACHED_TILL)
    }
}