package com.sloth.ScreenWatcher.ui.presentation.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    val registerResult = MutableLiveData<Result<Unit>>()

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            try {
                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    registerResult.postValue(Result.failure(Exception("Preencha todos os campos!")))
                    return@launch
                }

                // Chama o repositório para realizar o registro
                val result = repository.register(email, password, username)

                // Atualiza o resultado
                registerResult.postValue(result)

                // Log de sucesso ou falha
                if (result.isSuccess) {
                    Log.i("SCREEN_WATCHER", "Cadastro bem-sucedido!")
                } else {
                    Log.i("SCREEN_WATCHER", "Falha no cadastro: ${result.exceptionOrNull()?.message}")
                }

            } catch (e: Exception) {
                registerResult.postValue(Result.failure(Exception("Erro no cadastro: ${e.message}")))
                Log.i("SCREEN_WATCHER", "Erro no cadastro: ${e.message}")
            }
        }
    }
}