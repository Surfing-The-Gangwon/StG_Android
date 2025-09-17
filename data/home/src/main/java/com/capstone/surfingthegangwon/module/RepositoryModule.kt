package com.capstone.surfingthegangwon.module

import com.capstone.surfingthegangwon.repository.CityRepository
import com.capstone.surfingthegangwon.repository.SeashoreRepository
import com.capstone.surfingthegangwon.repositoryImpl.CityRepositoryImpl
import com.capstone.surfingthegangwon.repositoryImpl.SeashoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository

    @Binds
    @Singleton
    abstract fun bindSeashoreRepository(impl: SeashoreRepositoryImpl): SeashoreRepository
}