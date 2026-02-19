package com.particlesector.soniflac.core.network.di

import com.particlesector.soniflac.core.network.RadioBrowserApi
import com.particlesector.soniflac.core.network.RadioBrowserClient
import com.particlesector.soniflac.core.network.interceptor.UserAgentInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        radioBrowserClient: RadioBrowserClient,
    ): OkHttpClient {
        val baseUrlInterceptor = Interceptor { chain ->
            val resolvedBase = radioBrowserClient.getBaseUrlBlocking().toHttpUrl()
            val original = chain.request()
            val newUrl = original.url.newBuilder()
                .scheme(resolvedBase.scheme)
                .host(resolvedBase.host)
                .port(resolvedBase.port)
                .build()
            chain.proceed(original.newBuilder().url(newUrl).build())
        }

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(UserAgentInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                },
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRadioBrowserApi(
        okHttpClient: OkHttpClient,
        json: Json,
    ): RadioBrowserApi = Retrofit.Builder()
        .baseUrl("https://placeholder.radio-browser.info/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RadioBrowserApi::class.java)
}
