package com.sloth.ScreenWatcher.auth.domain.repository

import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import com.sloth.ScreenWatcher.util.Resource

interface AuthRepository {
    suspend fun login(username: String, password: String): Resource<AuthUser>

    suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<Unit>

    suspend fun logout()

    fun getCurrentUser()

    suspend fun updateUserStatus(isOnline: Boolean)
}