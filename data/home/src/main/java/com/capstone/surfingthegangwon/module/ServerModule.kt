package com.capstone.surfingthegangwon.module

import com.capstone.surfingthegangwon.api.ServerApi
import com.capstone.surfingthegangwon.core.retrofit.RetrofitModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerModule {

    @Provides
    @Singleton
    fun provideServerApi(
        @RetrofitModule.ServiceApi retrofit: Retrofit
    ): ServerApi {
        return retrofit.create(ServerApi::class.java)
    }
}