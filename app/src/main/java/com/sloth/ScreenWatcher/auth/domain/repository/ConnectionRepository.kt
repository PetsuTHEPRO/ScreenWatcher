package com.sloth.ScreenWatcher.auth.domain.repository

import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.auth.domain.model.ConnectionStatus
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatus
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatusPair

interface ConnectionRepository {
    suspend fun createConnection(user1: String, user2: String): Result<Connection>
    suspend fun getConnectionsForUser(username: String): Result<List<Connection>>
    suspend fun updateConnectionStatus(connectionId: String, status: ConnectionStatus): Result<Unit>
    suspend fun getStatusForUser(currentUser: String, targetUsername: String): Result<ScreenStatusPair>
}