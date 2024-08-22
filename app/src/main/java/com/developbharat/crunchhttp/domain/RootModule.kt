package com.developbharat.crunchhttp.domain

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpMethod
import com.developbharat.crunchhttp.common.Constants
import com.developbharat.crunchhttp.domain.data.database.MainDatabase
import com.developbharat.crunchhttp.domain.repos.device.DeviceDetails
import com.developbharat.crunchhttp.domain.repos.device.IDeviceDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RootModule {

    @Provides
    @Singleton
    fun providesDeviceDetails(@ApplicationContext appContext: Context): IDeviceDetails {
        val sharedPreferences = appContext.getSharedPreferences("SHARED_STORE", Context.MODE_PRIVATE)
        return DeviceDetails(context = appContext, sharedPreferences = sharedPreferences)
    }


    @Provides
    @Singleton
    fun providesApolloClient(deviceDetails: IDeviceDetails): ApolloClient {
        // Read device id
        val androidId = deviceDetails.useDeviceId()

        return ApolloClient.Builder()
            .serverUrl(Constants.GRAPHQL_URL)
            .addHttpHeader("Authorization", androidId)
            .addHttpHeader("Content-Type", "application/json")
            .httpMethod(HttpMethod.Post)
            .build()
    }

    @Provides
    @Singleton
    fun providesDatabaseInstance(@ApplicationContext context: Context, deviceDetails: IDeviceDetails): MainDatabase {
        val password = deviceDetails.useDatabasePassword()
        return MainDatabase.createDatabaseInstance(context, password)
    }
}