package com.developbharat.crunchhttp.domain

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpMethod
import com.developbharat.crunchhttp.common.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RootModule {

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    fun providesApolloClient(@ApplicationContext context: Context): ApolloClient {
        // Read device id
        val contentResolver = context.contentResolver
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        return ApolloClient.Builder()
            .serverUrl(Constants.GRAPHQL_URL)
            .addHttpHeader("Authorization", androidId)
            .addHttpHeader("Content-Type", "application/json")
            .httpMethod(HttpMethod.Post)
            .build()
    }

}