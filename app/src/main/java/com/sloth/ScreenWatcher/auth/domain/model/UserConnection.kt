package com.sloth.ScreenWatcher.auth.domain.model

// Novo modelo para conexões
data class UserConnection(
    val connectionId: String = "",       // ID único da conexão
    val user1: String = "",              // Usuário que iniciou
    val user2: String = "",              // Usuário alvo
    val status: ConnectionStatus = ConnectionStatus.PENDING,
    val lastUpdated: Long = System.currentTimeMillis()
)