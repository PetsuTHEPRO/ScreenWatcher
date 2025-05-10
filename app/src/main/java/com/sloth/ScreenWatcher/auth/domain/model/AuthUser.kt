package com.sloth.ScreenWatcher.auth.domain.model

data class AuthUser(
    val id: String = "",
    val basicInfo: BasicInfo = BasicInfo(),
    val status: UserStatus = UserStatus(),
    val connections: UserConnection = UserConnection()
)