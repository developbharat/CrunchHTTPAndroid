package com.developbharat.crunchhttp.domain.modules

import android.content.Context
import com.developbharat.crunchhttp.domain.data.api.IAccountsRemoteAPI
import com.developbharat.crunchhttp.domain.repos.accounts.AccountsRepository
import com.developbharat.crunchhttp.domain.repos.accounts.IAccountsRepository
import com.developbharat.crunchhttp.domain.repos.android.AndroidRepository
import com.developbharat.crunchhttp.domain.repos.android.IAndroidRepository
import com.developbharat.crunchhttp.domain.store.ISharedStore
import com.developbharat.crunchhttp.domain.store.SharedStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RootModule {

    @Provides
    @Singleton
    fun providesGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun providesSharedStore(@ApplicationContext() context: Context, gson: Gson): ISharedStore {
        val sharedPreferences = context.getSharedPreferences("SHARED_STORE", Context.MODE_PRIVATE)
        return SharedStore(sharedPreferences, gson)
    }

    @Provides
    @Singleton
    fun providesAndroidRepository(@ApplicationContext() context: Context): IAndroidRepository {
        return AndroidRepository(context)
    }

    @Provides
    @Singleton
    fun providesAccountsRemoteAPI(retrofit: Retrofit): IAccountsRemoteAPI {
        return retrofit.create(IAccountsRemoteAPI::class.java)
    }

    @Provides
    @Singleton
    fun providesAccountsRepository(
        api: IAccountsRemoteAPI,
        sharedStore: ISharedStore,
    ): IAccountsRepository = AccountsRepository(api, sharedStore)
}