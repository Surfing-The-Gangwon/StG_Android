package com.capstone.surfingthegangwon.core.retrofit

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoApi

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ServiceApi

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TourApi

    @Provides
    @Singleton
    fun provideDefaultOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor { msg -> android.util.Log.d("OkHttp", msg) }
        logger.level = when {
            BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.HEADERS // or BASIC
            else -> HttpLoggingInterceptor.Level.NONE
        }
        return logger
    }

    @Provides
    @Singleton
    @ServiceApi
    fun provideServiceOkHttpClient(
        authHeaderInterceptor: AuthHeaderInterceptor,
        logging: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 (기본 10초)
            .readTimeout(30, TimeUnit.SECONDS)    // 응답 타임아웃 (기본 10초)
            .writeTimeout(30, TimeUnit.SECONDS)   // 업로드 타임아웃 (파일 전송에 중요)build()
            .retryOnConnectionFailure(true)
            .addInterceptor(authHeaderInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @ServiceApi
    fun provideServiceApiRetrofit(@ServiceApi client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    @TourApi
    fun provideTourOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                TourQueryInterceptor(
                    serviceKey = BuildConfig.TOUR_API_KEY
                )
            )
            .addInterceptor(DebugResponseInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    @TourApi
    fun provideTourApiRetrofit(@TourApi client: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.TOUR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
}