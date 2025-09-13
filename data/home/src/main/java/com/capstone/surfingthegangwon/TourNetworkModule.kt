package com.capstone.surfingthegangwon

import com.capstone.surfingthegangwon.core.retrofit.RetrofitModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TourNetworkModule {
    @Provides
    @Singleton
    fun provideTourApiService(@RetrofitModule.TourApi retrofit: Retrofit): TourApiService =
        retrofit.create(TourApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TourRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTourRepository(
        impl: TourRepositoryImpl
    ): TourRepository
}