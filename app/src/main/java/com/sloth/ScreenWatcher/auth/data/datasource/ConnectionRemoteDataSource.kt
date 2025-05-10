package com.sloth.ScreenWatcher.auth.data.datasource

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.sloth.ScreenWatcher.auth.data.session.SessionManager
import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.auth.domain.model.ConnectionStatus
import com.sloth.ScreenWatcher.auth.domain.model.MyConnection
import com.sloth.ScreenWatcher.auth.domain.model.ScreenStatusPair
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConnectionRemoteDataSource(
    private val sessionManager: SessionManager,
    private val realtimeDb: DatabaseReference
) {

    private companion object {
        const val CONNECTIONS_PATH = "connections"
    }

    suspend fun createConnection(
        connection: Connection,
        initialStatus: String = "PENDING"): Result<Unit>
    {
        Log.i("SCREEN_WATCHER", "Creating connection(Data Source): $connection")
        return try {
            // Salva a conexão completa no RealtimeDB
            realtimeDb.child(CONNECTIONS_PATH).child(connection.id).setValue(connection).await()

            // 2. Atualização atômica em múltiplos locais
            val updates = hashMapOf<String, Any>(
                // Nó principal de conexões
                "$CONNECTIONS_PATH/${connection.id}" to connection,

                // Conexão no usuário atual
                "users/${connection.user1}/connections/${connection.user2}" to mapOf(
                    "status" to initialStatus,
                    "connectionId" to connection.id,
                    "createdAt" to ServerValue.TIMESTAMP
                ),

                // Conexão no usuário alvo
                "users/${connection.user2}/connections/${connection.user1}" to mapOf(
                    "status" to "PENDING", // Status inicial para o target
                    "connectionId" to connection.id,
                    "createdAt" to ServerValue.TIMESTAMP
                )
            )

            // 3. Executa todas as operações atomicamente
            realtimeDb.updateChildren(updates).await()
            Log.i("SCREEN_WATCHER", "Connection created: $connection")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SCREEN_WATCHER", "Error creating connection: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchConnectionsForUser(username: String): Result<List<MyConnection>> {
        return try {
            // 1. Busca a lista de conexões do usuário
            val userConnectionsRef = realtimeDb.child("users")
                .child(username)
                .child("connections")

            val userConnectionsSnapshot = userConnectionsRef.get().await()

            // Debug: verifique a estrutura real
            Log.d("SCREEN_WATCHER", "Estrutura completa: ${userConnectionsSnapshot.value}")

            // 2. Mapeia os dados das conexões diretamente para MyConnection
            val connections = userConnectionsSnapshot.children.mapNotNull { snapshot ->
                try {
                    // Converte cada snapshot em um objeto MyConnection
                    snapshot.getValue(MyConnection::class.java)?.copy(connectionId = snapshot.key ?: "")
                } catch (e: Exception) {
                    Log.e("Firebase", "Erro ao mapear conexão", e)
                    null
                }
            }

            Log.d("SCREEN_WATCHER", "Conexões encontradas: $connections")

            // 3. Retorna as conexões mapeadas
            Result.success(connections)
        } catch (e: Exception) {
            Log.e("Firebase", "Erro ao buscar conexões", e)
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

    fun observeScreenStatus(): Flow<String> = callbackFlow {
        val targetUsername = getConnectionId()!!
        val username = getCurrentUser()
        val combinedKey = getConnectionId(username, targetUsername)

        val ref = combinedKey.getOrNull()?.let { realtimeDb.child("connections").child(it) }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user1 = snapshot.child("user1").getValue(String::class.java) ?: ""
                val statusField = if (username == user1) "screenStatus2" else "screenStatus1"
                val status = snapshot.child(statusField).getValue(String::class.java) ?: "OFF"
                trySend(status)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        if (ref != null) {
            ref.addValueEventListener(listener)
        }

        awaitClose {
            if (ref != null) {
                ref.removeEventListener(listener)
            }
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
                Log.e("SCREEN_WATCHER", "Error fetching status: ${error.message}")
                continuation.resume(Result.failure(Exception(error.message)))
            }
        })
    }

    fun getCurrentUser(): String {
        return sessionManager.getUsername() ?: throw IllegalStateException("Username not found")
    }

    fun logout() {
        sessionManager.logout()
    }

    suspend fun getConnectionId(username: String, target: String): Result<String> {
        return try {
            // Referência para a conexão específica no Firebase
            val ref = realtimeDb.child("users")
                .child(username)
                .child("connections")
                .child(target)

            // Obtém o snapshot de forma assíncrona
            val snapshot = ref.get().await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Conexão não encontrada entre $username e $target"))
            }

            // Obtém o connectionId
            val connectionId = snapshot.child("connectionId").getValue(String::class.java)

            connectionId?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Campo connectionId não encontrado ou está vazio"))

        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar connectionId: ${e.message}", e))
        }
    }

    suspend fun updateScreenStatusForUser(username: String, status: String): Result<Unit> {
        // Referência para as conexões no Firebase
        val ref = realtimeDb.child("connections")

        // Buscar as conexões para o usuário
        val result = this.fetchConnectionsForUser(username)

        // Verifique se o resultado da busca foi bem-sucedido
        if (result.isFailure) {
            // Se houver falha, retorna o erro
            return Result.failure(result.exceptionOrNull() ?: Exception("Erro desconhecido"))
        }

        // Iterando sobre as conexões e montando a chave combinada
        val myConnection = result.getOrNull() ?: return Result.failure(Exception("Conexões não encontradas"))

        val updateTasks = mutableListOf<Task<Void>>() // Lista de tasks para sincronização

        for (connection in myConnection) {
            val targetUsername = connection.connectionId
            var combinedKey = ""
            val resultConnecton: Result<String> = getConnectionId(username, targetUsername)  // Monta a chave combinada

            if (resultConnecton.isSuccess){
                combinedKey = resultConnecton.getOrNull().toString() // Pega a String do sucesso
                println("Connection ID: $combinedKey")
            }

            val snapshot = ref.child(combinedKey).child("user1").get().await()
            val user1 = snapshot.getValue(String::class.java) ?: ""
            var screenTela: String?

            if(username == user1){
                screenTela = "screenStatus1"
            }else{
                screenTela = "screenStatus2"
            }

            val connectionRef = ref.child(combinedKey).child(screenTela)

            // Adiciona a tarefa de atualização na lista de tarefas
            val task = connectionRef.setValue(status)
            updateTasks.add(task)
        }

        // Espera todas as tarefas de atualização serem concluídas
        try {
            // `await()` não está disponível diretamente em `Task`, então você pode usar `Tasks.whenAllSuccess()`
            Tasks.whenAllSuccess<Void>(*updateTasks.toTypedArray()).await()
            return Result.success(Unit)  // Retorna sucesso se todas as atualizações forem feitas
        } catch (e: Exception) {
            // Se houver falha, retorna o erro
            return Result.failure(e)
        }
    }

    fun setConnectionId(connectionId: String) {
        sessionManager.setCurrentConnection(connectionId)
    }

    fun getConnectionId(): String? {
        return sessionManager.getCurrentConnection()
    }

    suspend fun getScreenStatus(): String? {
        return try{
            val snapshot = realtimeDb.child("connections")
            val targetUsername = this.getConnectionId()!!
            val username = this.getCurrentUser()

            var combinedKey = this.getConnectionId(username, targetUsername)
            Log.i("SCREEN_WATCHER", "Combined Key: ${combinedKey.getOrNull()}")

            val screenStatus1 = combinedKey.getOrNull()
                ?.let { snapshot.child(it).child("screenStatus1").get().await().getValue(String::class.java) }
                ?: "Tela Indisponível"

            Log.i("SCREEN_WATCHER", "Screen Status: $screenStatus1")

            return screenStatus1
        }catch (e: Exception){
            "Tela Indisponível"
        }
    }

}


