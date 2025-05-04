package com.example.ScreenWatcher.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sloth.ScreenWatcher.auth.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    val loginResult = MutableLiveData<Result<Unit>>()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                if (username.isEmpty() || password.isEmpty()) {
                    loginResult.postValue(Result.failure(Exception("Preencha todos os campos!")))
                    return@launch
                }

                val result = repository.login(username, password)

                // Sucesso no login
                if (result == null) {
                    Log.i("SCREEN_WATCHER", "Login bem-sucedido!")
                } else {
                    Log.i("SCREEN_WATCHER", "Login mal-sucedido!")
                }

                delay(2000)
            } catch (e: Exception) {
                loginResult.postValue(Result.failure(Exception("Erro no login: ${e.message}")))
                Log.i("SCREEN_WATCHER", "Erro no login: ${e.message}")
            }
        }
    }
}