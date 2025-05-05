package com.sloth.ScreenWatcher.auth.domain.model

data class BasicInfo(
    val username: String = "",
    val email: String = "",
    val passwordHash: String = "", // hash da senha
    val createdAt: Long = 0
)