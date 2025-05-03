package com.sloth.ScreenWatcher.auth.domain.repository

import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import com.sloth.ScreenWatcher.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<AuthUser>

    suspend fun register(
        email: String,
        password: String,
        username: String
    ): Resource<AuthUser>

    suspend fun logout(): Resource<Unit>

    fun getCurrentUser(): AuthUser?

    suspend fun updateUserStatus(isOnline: Boolean): Resource<Unit>
}