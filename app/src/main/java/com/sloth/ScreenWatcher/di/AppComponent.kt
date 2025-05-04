package com.sloth.ScreenWatcher.di

import androidx.test.espresso.core.internal.deps.dagger.Component
import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseModule::class, AuthModule::class])
interface AppComponent {
    fun getAuthRemoteDataSource(): AuthRemoteDataSource

    fun getAutRepository(): AuthRepository
}
