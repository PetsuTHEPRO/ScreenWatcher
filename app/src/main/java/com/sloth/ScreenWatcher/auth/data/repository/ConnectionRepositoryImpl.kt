package com.sloth.ScreenWatcher.auth.data.repository

import android.util.Log
import com.sloth.ScreenWatcher.auth.data.datasource.ConnectionRemoteDataSource
import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.auth.domain.model.ConnectionStatus
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatus
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatusPair
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository


class ConnectionRepositoryImpl (
    private val remoteDataSource: ConnectionRemoteDataSource
) : ConnectionRepository {

    override suspend fun createConnection(user1: String, user2: String): Result<Connection> {
        return try {
            val newConnection = Connection(
                id = Connection.generateId(user1, user2),
                user1 = user1,
                user2 = user2,
                status = ConnectionStatus.PENDING,
                screenStatus1 = ScreenStatus.OFF,
                screenStatus2 = ScreenStatus.OFF,
                photoUrl = ""
            )
            Log.i("SCREEN_WATCHER", "New connection: $newConnection")
            remoteDataSource.createConnection(newConnection).map { newConnection }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getConnectionsForUser(username: String): Result<List<Connection>> {
        return remoteDataSource.fetchConnectionsForUser(username)
    }

    override suspend fun updateConnectionStatus(
        connectionId: String,
        status: ConnectionStatus
    ): Result<Unit> {
        return remoteDataSource.updateConnectionStatus(connectionId, status)
    }

    override suspend fun getStatusForUser(
        currentUser: String,
        targetUsername: String
    ): Result<ScreenStatusPair> {
        return remoteDataSource.getStatusForUser(currentUser, targetUsername)
    }
}