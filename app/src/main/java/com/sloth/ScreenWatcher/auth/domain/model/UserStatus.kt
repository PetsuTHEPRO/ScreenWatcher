package com.sloth.ScreenWatcher.auth.domain.model

data class UserStatus(
    val isOnline: Boolean = false,
    val lastActivity: Long = 0
)
