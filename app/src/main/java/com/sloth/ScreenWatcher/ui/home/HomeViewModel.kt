package com.sloth.ScreenWatcher.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository
import com.sloth.ScreenWatcher.ui.connections.ConnectionViewModel.ConnectionState
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ConnectionRepository
) : ViewModel() {

    private val _statusTela = MutableLiveData<Boolean>().apply {
        value = true
    }
    val statusTela: LiveData<Boolean> = _statusTela

    fun checkScreenStatuses(currentUser: String, targetUsername: String) {
        viewModelScope.launch {
            val result = repository.getStatusForUser(currentUser, targetUsername)
            result.onSuccess { pair ->
                Log.d("ViewModel", "status1: ${pair.screenStatus1}, status2: ${pair.screenStatus2}")
            }.onFailure {
                Log.e("ViewModel", "Erro ao buscar status", it)
            }
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}