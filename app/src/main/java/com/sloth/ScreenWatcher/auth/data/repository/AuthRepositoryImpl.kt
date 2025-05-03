package com.sloth.ScreenWatcher.auth.data.repository

import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import com.sloth.ScreenWatcher.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthUser> {
        return try {
            val user = remoteDataSource.login(email, password)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): Resource<AuthUser> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): AuthUser? {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserStatus(isOnline: Boolean): Resource<Unit> {
        TODO("Not yet implemented")
    }
}