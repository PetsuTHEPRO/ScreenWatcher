package com.sloth.ScreenWatcher.auth.data.repository

import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import com.sloth.ScreenWatcher.util.Resource

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(username: String, password: String): Resource<AuthUser> {
        return try {
            val user = remoteDataSource.login(username, password)

            // Se o login retornar usuário vazio, trata como erro
            if (user.basicInfo.username.isEmpty()) {
                Resource.Error("Usuário ou senha incorretos")
            } else {
                Resource.Success(user)
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido no login")
        }
    }

    override suspend fun register(email: String, password: String, username: String): Result<Unit> {
        return remoteDataSource.register(email, password, username)
    }

    override suspend fun logout(){
        TODO("Not yet implemented")
    }

    override fun getCurrentToken(): String? {
        return remoteDataSource.getCurrentUsername()
    }

    override suspend fun updateUserStatus(isOnline: Boolean) {
        TODO("Not yet implemented")
    }
}