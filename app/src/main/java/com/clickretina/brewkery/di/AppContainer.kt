package com.clickretina.brewkery.di

import android.content.Context
import com.clickretina.brewkery.data.local.BrewkeryLocalStore
import com.clickretina.brewkery.data.remote.BrewkeryApiService
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val repository: BrewkeryRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BrewkeryApiService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: BrewkeryApiService by lazy {
        retrofit.create(BrewkeryApiService::class.java)
    }

    private val localStore: BrewkeryLocalStore by lazy {
        BrewkeryLocalStore(context)
    }

    override val repository: BrewkeryRepository by lazy {
        BrewkeryRepository(apiService, localStore)
    }
}
