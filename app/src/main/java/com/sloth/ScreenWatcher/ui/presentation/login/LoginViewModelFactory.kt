package com.sloth.ScreenWatcher.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ScreenWatcher.ui.login.LoginViewModel
import com.sloth.ScreenWatcher.auth.data.repository.AuthRepositoryImpl

class LoginViewModelFactory(private val authRepository: AuthRepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}