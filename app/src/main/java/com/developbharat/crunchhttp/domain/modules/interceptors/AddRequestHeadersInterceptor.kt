package com.developbharat.crunchhttp.domain.modules.interceptors

import com.developbharat.crunchhttp.common.SharedStoreKeys
import com.developbharat.crunchhttp.domain.models.accounts.UserAccount
import com.developbharat.crunchhttp.domain.store.ISharedStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AddRequestHeadersInterceptor @Inject constructor(private val sharedStore: ISharedStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // fetch auth token from shared cache or put blank auth token if not found
        val isUserAccountAvailable = sharedStore.isValueAvailable(SharedStoreKeys.USER_ACCOUNT)
        val authToken = if (isUserAccountAvailable) {
            sharedStore.useValue(SharedStoreKeys.USER_ACCOUNT, UserAccount::class.java).authToken
        } else {
            ""
        }

        val req = originalRequest
            .newBuilder()
            .addHeader("content-type", "application/json")
            .addHeader("Authorization", authToken)
            .build()
        return chain.proceed(req)
    }
}