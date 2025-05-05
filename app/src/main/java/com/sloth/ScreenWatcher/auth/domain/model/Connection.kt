package com.sloth.ScreenWatcher.auth.domain.model

enum class ConnectionStatus { PENDING, ACTIVE, BLOCKED }
enum class ScreenStatus { ON, OFF }

data class Connection(
    val id: String, // Formato: "username1_username2"
    val user1: String, // Quem enviou o pedido
    val user2: String, // Quem recebeu
    val status: ConnectionStatus,
    val screenStatus1: ScreenStatus,
    val screenStatus2: ScreenStatus,
    val photoUrl: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun generateId(user1: String, user2: String): String {
            return "${user1}_$user2".lowercase()
        }
    }
}