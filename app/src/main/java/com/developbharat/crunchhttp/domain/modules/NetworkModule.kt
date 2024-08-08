package com.developbharat.crunchhttp.domain.modules

import com.developbharat.crunchhttp.common.Constants
import com.developbharat.crunchhttp.domain.modules.interceptors.AddRequestHeadersInterceptor
import com.developbharat.crunchhttp.domain.store.ISharedStore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedStore: ISharedStore): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AddRequestHeadersInterceptor(sharedStore))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}