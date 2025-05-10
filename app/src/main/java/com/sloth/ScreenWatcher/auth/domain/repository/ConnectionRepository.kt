package com.sloth.ScreenWatcher.auth.domain.repository

import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.auth.domain.model.ConnectionStatus
import com.sloth.ScreenWatcher.auth.domain.model.MyConnection
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatusPair
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
    suspend fun createConnection(user1: String, user2: String): Result<Connection>
    suspend fun getConnectionsForUser(username: String): Result<List<MyConnection>>
    suspend fun updateConnectionStatus(connectionId: String, status: ConnectionStatus): Result<Unit>
    suspend fun getStatusForUser(currentUser: String, targetUsername: String): Result<ScreenStatusPair>
    suspend fun getCurrentUser(): String
    suspend fun updateScreenStatusForUser(username: String, status: String): Result<Unit>
    fun setCurrentConnectionId(connectionId: String?)
    fun logout()
    fun getCurrentConnectionId(): String?
    suspend fun getScreenStatus(): String?
    // Observação em tempo real (MODIFICADO)
    fun observeScreenStatus(): Flow<String>

}