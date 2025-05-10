package com.sloth.ScreenWatcher.ui.connections

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.auth.domain.model.ConnectionStatus
import com.sloth.ScreenWatcher.auth.domain.model.MyConnection
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConnectionViewModel (
    private val repository: ConnectionRepository
) : ViewModel() {

    // Dados observáveis para cada conexão
    val connection1 = MutableLiveData<MyConnection>()
    val connection2 = MutableLiveData<MyConnection>()
    val connection3 = MutableLiveData<MyConnection>()
    val connection4 = MutableLiveData<MyConnection>()
    val _currentUser = MutableLiveData<String>()
    val currentUser: LiveData<String> = _currentUser

    // Função direta (não suspensa) que retorna o valor imediatamente
    fun getCurrentUser(): String? {
        return runBlocking {
            try {
                repository.getCurrentUser()
            } catch (e: Exception) {
                Log.e("SCREEN_WATCHER", "Erro ao buscar usuário", e)
                null
            }
        }
    }

    // Sealed class para encapsular o resultado
    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Throwable) : Result<Nothing>()
    }

    // Estados possíveis para as operações de conexão
    sealed class ConnectionState {
        object Idle : ConnectionState()
        object Loading : ConnectionState()
        data class Success(val connection: Connection) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    // Estados para a lista de conexões
    sealed class ConnectionListState {
        object Loading : ConnectionListState()
        data class Success(val connections: List<MyConnection>) : ConnectionListState()
        data class Error(val message: String) : ConnectionListState()
    }

    // LiveData para estados individuais
    private val _connectionState = MutableLiveData<ConnectionState>(ConnectionState.Idle)
    val connectionState: LiveData<ConnectionState> = _connectionState

    // LiveData para a lista de conexões
    private val _connectionListState = MutableLiveData<ConnectionListState>()
    val connectionListState: LiveData<ConnectionListState> = _connectionListState

    // Cria uma nova conexão
    fun createConnection(currentUser: String, targetUsername: String) {
        _connectionState.value = ConnectionState.Loading
        viewModelScope.launch {
            Log.i("SCREEN_WATCHER", "Creating connection with $targetUsername")
            repository.createConnection(currentUser, targetUsername)
                .onSuccess { connection ->
                    _connectionState.value = ConnectionState.Success(connection)
                    loadConnections(currentUser) // Recarrega a lista após criar
                }
                .onFailure { e ->
                    _connectionState.value = ConnectionState.Error(e.message ?: "Erro desconhecido")
                }
        }
    }

    fun loadConnections(username: String) {
        _connectionListState.value = ConnectionListState.Loading
        viewModelScope.launch {
            repository.getConnectionsForUser(username)
                .onSuccess { connections ->
                    // Ordena as conexões pela data de criação, mais antigas primeiro
                    val sortedConnections = connections.sortedBy { it.createdAt } // Assuming 'createdAt' exists

                    _connectionListState.value = ConnectionListState.Success(sortedConnections)

                    // Atualiza as conexões observáveis para cada posição do grid
                    updateLiveDataWithConnections(sortedConnections)
                }
                .onFailure { e ->
                    _connectionListState.value = ConnectionListState.Error(e.message ?: "Erro ao carregar conexões")
                }
        }
    }

    private fun updateLiveDataWithConnections(connections: List<MyConnection>) {
        // Preenche as conexões nos slots da UI, até o máximo de 4
        connection1.value = connections.getOrNull(0)
        connection2.value = connections.getOrNull(1)
        connection3.value = connections.getOrNull(2)
        connection4.value = connections.getOrNull(3)
    }

    // Atualiza o status de uma conexão
    fun updateConnectionStatus(connectionId: String, status: ConnectionStatus) {
        viewModelScope.launch {
            repository.updateConnectionStatus(connectionId, status)
                .onFailure { e ->
                    _connectionState.value = ConnectionState.Error(e.message ?: "Erro ao atualizar status")
                }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun setConnectionId(connectionId: String?) {
        repository.setCurrentConnectionId(connectionId)
    }

    fun getConnectionId(): String? {
        return repository.getCurrentConnectionId()
    }
}