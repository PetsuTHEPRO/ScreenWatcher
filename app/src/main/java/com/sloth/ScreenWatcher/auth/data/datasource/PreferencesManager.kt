package com.sloth.ScreenWatcher.auth.data.datasource

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(private val context: Context) {
    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    // Salva um valor String
    fun saveString(key: String, value: String) {
        sharedPref.edit().putString(key, value).apply()
    }

    // Obtém um valor String (nullable)
    fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }

    // Remove uma chave
    fun remove(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    // Limpa todas as preferências
    fun clear() {
        sharedPref.edit().clear().apply()
    }
}