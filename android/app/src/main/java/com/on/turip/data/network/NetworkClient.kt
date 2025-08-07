package com.on.turip.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.on.turip.BuildConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber

object NetworkClient {
    private const val BASE_URL = BuildConfig.BASE_URL
    private const val LOG_PREFIX = "moongjenut"
    private val contentType = "application/json".toMediaType()

    private val json =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    private val client: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message -> logHttpMessage(message) }.apply {
            level =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }

    private fun logHttpMessage(message: String) {
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val prettyJson =
                    json.encodeToString(
                        JsonElement.serializer(),
                        Json.parseToJsonElement(message),
                    )
                Timber.tag(LOG_PREFIX).d(prettyJson)
            } catch (e: Exception) {
                Timber.tag(LOG_PREFIX).d(message)
            }
        } else {
            Timber.tag(LOG_PREFIX).d(message)
        }
    }

    val turipNetwork: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
