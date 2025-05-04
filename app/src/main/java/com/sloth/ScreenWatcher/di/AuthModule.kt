package com.sloth.ScreenWatcher.di

import androidx.test.espresso.core.internal.deps.dagger.Module
import androidx.test.espresso.core.internal.deps.dagger.Provides
import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.data.repository.AuthRepositoryImpl
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
class AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        remoteDataSource: AuthRemoteDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(remoteDataSource)
    }
}
