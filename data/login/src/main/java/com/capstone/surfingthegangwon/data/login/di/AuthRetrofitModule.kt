package com.capstone.surfingthegangwon.data.login.di

import com.capstone.surfingthegangwon.core.retrofit.RetrofitModule
import com.capstone.surfingthegangwon.data.login.api.ServerAuthRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthRetrofitModule {

    @Provides
    @Singleton
    fun provideServerAuthRetrofitService(
        @RetrofitModule.ServiceApi retrofit: Retrofit): ServerAuthRetrofitService {
        return retrofit.create(ServerAuthRetrofitService::class.java)
    }
}