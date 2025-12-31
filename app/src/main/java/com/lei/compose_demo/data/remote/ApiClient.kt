package com.lei.compose_demo.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lei.compose_demo.data.remote.NetworkConfig.BASE_URL
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Retrofit 客户端构建器。
 */
object ApiClient {
    // JSON 解析配置。
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    // 日志拦截器。
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttp 客户端。
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit 实例。
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    // API 服务实例。
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
