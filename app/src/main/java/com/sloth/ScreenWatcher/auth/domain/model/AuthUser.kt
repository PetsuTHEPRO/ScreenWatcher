package com.sloth.ScreenWatcher.auth.domain.model

import java.util.Date

data class AuthUser(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val createdAt: Date = Date()
)