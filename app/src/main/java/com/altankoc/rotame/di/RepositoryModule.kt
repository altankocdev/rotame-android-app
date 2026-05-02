package com.altankoc.rotame.di

import com.altankoc.rotame.feature.auth.data.repository.AuthRepositoryImpl
import com.altankoc.rotame.feature.auth.domain.repository.AuthRepository
import com.altankoc.rotame.feature.location.data.repository.LocationRepositoryImpl
import com.altankoc.rotame.feature.location.domain.repository.LocationRepository
import com.altankoc.rotame.feature.profile.data.repository.ProfileRepositoryImpl
import com.altankoc.rotame.feature.profile.domain.repository.ProfileRepository
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
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}