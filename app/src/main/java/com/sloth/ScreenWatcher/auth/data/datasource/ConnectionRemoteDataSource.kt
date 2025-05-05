package com.sloth.ScreenWatcher.auth.data.datasource

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.auth.domain.model.ConnectionStatus
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatus
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatusPair
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConnectionRemoteDataSource(
    private val realtimeDb: DatabaseReference
) {

    private companion object {
        const val CONNECTIONS_PATH = "connections"
    }

    suspend fun createConnection(connection: Connection): Result<Unit> {
        Log.i("SCREEN_WATCHER", "Creating connection(Data Source): $connection")
        return try {
            // Salva a conexão completa no RealtimeDB
            realtimeDb.child(CONNECTIONS_PATH).child(connection.id).setValue(connection).await()
            Log.i("SCREEN_WATCHER", "Connection created: $connection")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SCREEN_WATCHER", "Error creating connection: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchConnectionsForUser(username: String): Result<List<Connection>> {
        return try {
            val snapshot = realtimeDb.child(CONNECTIONS_PATH)
                .orderByChild("users/$username")
                .equalTo(true)
                .get()
                .await()

            val connections = snapshot.children.mapNotNull { it.getValue(Connection::class.java) }
            Result.success(connections)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateConnectionStatus(
        connectionId: String,
        status: ConnectionStatus
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            realtimeDb.child(CONNECTIONS_PATH).child(connectionId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Método adicional para observar mudanças em tempo real
    fun observeConnection(connectionId: String): Flow<Connection> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Connection::class.java)?.let { connection ->
                    trySend(connection)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        realtimeDb.child(CONNECTIONS_PATH).child(connectionId)
            .addValueEventListener(listener)

        awaitClose {
            realtimeDb.child(CONNECTIONS_PATH).child(connectionId)
                .removeEventListener(listener)
        }
    }

    suspend fun getStatusForUser(
        currentUser: String,
        targetUsername: String
    ): Result<ScreenStatusPair> = suspendCoroutine { continuation ->

        val combinedKey = "${currentUser}_${targetUsername}"
        val ref = realtimeDb.child("connections").child(combinedKey)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status1 = snapshot.child("screenStatus1").getValue(Boolean::class.java)
                val status2 = snapshot.child("screenStatus2").getValue(Boolean::class.java)

                continuation.resume(Result.success(ScreenStatusPair(status1, status2)))
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(Result.failure(Exception(error.message)))
            }
        })
    }

}


