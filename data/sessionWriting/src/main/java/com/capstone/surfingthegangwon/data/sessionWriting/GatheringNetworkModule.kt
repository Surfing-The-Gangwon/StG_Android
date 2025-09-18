package com.capstone.surfingthegangwon.data.sessionWriting

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GatheringNetworkModule {

    @Provides
    @Singleton
    fun provideGatheringService(retrofit: Retrofit): GatheringService =
        retrofit.create(GatheringService::class.java)
}