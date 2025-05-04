package com.sloth.ScreenWatcher.auth.data.repository

import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import com.sloth.ScreenWatcher.util.Resource

class AuthRepositoryImpl(
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

    override suspend fun register(email: String, password: String, username: String): Result<Unit> {
        return remoteDataSource.register(email, password, username)
    }

    override suspend fun logout(){
        TODO("Not yet implemented")
    }

    override fun getCurrentUser() {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserStatus(isOnline: Boolean) {
        TODO("Not yet implemented")
    }
}