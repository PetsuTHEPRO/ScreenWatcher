package com.sloth.ScreenWatcher.auth.data.session

import com.sloth.ScreenWatcher.auth.data.datasource.PreferencesManager

class SessionManager(
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val USERNAME = "username"
        private const val CURRENT_CONNECTION = "current_connection"
        private const val EMAIL = "email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Salva o token do usuário
    fun saveUserToken(username: String, email: String) {
        preferencesManager.saveString(USERNAME, username)
        preferencesManager.saveString(EMAIL, email)
        preferencesManager.saveString(KEY_IS_LOGGED_IN, "true")
    }

    // Verifica se o usuário está logado
    fun isLoggedIn(): Boolean {
        return preferencesManager.getString(KEY_IS_LOGGED_IN) == "true"
    }

    fun getUsername(): String? {
        return preferencesManager.getString(USERNAME)
    }

    fun getCurrentConnection(): String? {
        return preferencesManager.getString(CURRENT_CONNECTION)
    }

    fun setCurrentConnection(connectionId: String) {
        preferencesManager.saveString(CURRENT_CONNECTION, connectionId)
    }

    // Finaliza a sessão
    fun logout() {
        preferencesManager.remove(USERNAME)
        preferencesManager.remove(EMAIL)
        preferencesManager.remove(CURRENT_CONNECTION)
        preferencesManager.saveString(KEY_IS_LOGGED_IN, "false")
    }
}