package com.sloth.ScreenWatcher.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import com.sloth.ScreenWatcher.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> get() = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _loginResult.postValue(Result.failure(Exception("Preencha todos os campos!")))
                return@launch
            }

            try {
                Log.d("SCREEN_WATCHER", "Iniciando login para $username")

                when (val result = repository.login(username, password)) {
                    is Resource.Success -> {
                        Log.d("SCREEN_WATCHER", "Login bem-sucedido")
                        _loginResult.postValue(Result.success(Unit))
                    }
                    is Resource.Error -> {
                        val errorMessage = (result as Resource.Error).error  // Acessando a propriedade 'error'
                        _loginResult.postValue(Result.failure(Exception(errorMessage)))
                    }
                    else -> {} // Ignore ou adicione outro tratamento, se necessário
                }

            } catch (e: Exception) {
                Log.e("SCREEN_WATCHER", "Exceção ao fazer login", e)
                _loginResult.postValue(Result.failure(e))
            }
        }
    }

}