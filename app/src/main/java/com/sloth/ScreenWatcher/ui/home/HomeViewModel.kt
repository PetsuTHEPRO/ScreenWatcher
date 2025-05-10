package com.sloth.ScreenWatcher.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ConnectionRepository
): ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    init {
        observeScreenStatus()
    }

    private fun observeScreenStatus() {
        viewModelScope.launch {
            try {

                repository.observeScreenStatus().collect { status ->
                    _text.value = when (status) {
                        "true" -> "ON"
                        "false" -> "OFF"
                        else -> status
                    }
                }
            } catch (e: Exception) {
                _text.value = "Erro: ${e.message}"
            }
        }
    }
}