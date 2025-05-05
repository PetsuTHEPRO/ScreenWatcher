package com.sloth.ScreenWatcher.ui.connections

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.auth.domain.model.ConnectionStatus
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository
import kotlinx.coroutines.launch

class ConnectionViewModel (
    private val repository: ConnectionRepository
) : ViewModel() {

    // Dados observáveis para cada conexão
    val connection1 = MutableLiveData<Connection>()
    val connection2 = MutableLiveData<Connection>()
    val connection3 = MutableLiveData<Connection>()
    val connection4 = MutableLiveData<Connection>()

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
        data class Success(val connections: List<Connection>) : ConnectionListState()
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

    // Carrega todas as conexões do usuário
    fun loadConnections(username: String) {
        _connectionListState.value = ConnectionListState.Loading
        viewModelScope.launch {
            repository.getConnectionsForUser(username)
                .onSuccess { connections ->
                    _connectionListState.value = ConnectionListState.Success(connections)
                }
                .onFailure { e ->
                    _connectionListState.value = ConnectionListState.Error(e.message ?: "Erro ao carregar conexões")
                }
        }
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
}