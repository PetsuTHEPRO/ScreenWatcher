package com.sloth.ScreenWatcher.ui.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository

class ConnectionViewModelFactory(
    private val connectionsRepository: ConnectionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ConnectionViewModel::class.java) -> {
                ConnectionViewModel(connectionsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}