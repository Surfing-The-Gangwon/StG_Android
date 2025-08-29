package com.capstone.surfingthegangwon.core.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    @Singleton
    abstract fun bindPreferenceStorage(
        impl: SharedPrefsStorage
    ): PreferenceStorage
}
