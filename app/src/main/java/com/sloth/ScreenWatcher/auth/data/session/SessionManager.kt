package com.sloth.ScreenWatcher.auth.data.session

import android.content.Context
import com.sloth.ScreenWatcher.auth.domain.model.BasicInfo

// SessionManager.kt
class SessionManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    fun saveUserSession(user: BasicInfo) {
        editor.putString("USERNAME", user.username)
        editor.putString("EMAIL", user.email)
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.apply()
    }

    fun getCurrentUser(): BasicInfo? {
        return if (sharedPref.getBoolean("IS_LOGGED_IN", false)) {
            BasicInfo(
                username = sharedPref.getString("USERNAME", "")!!,
                email = sharedPref.getString("EMAIL", "")!!,
            )
        } else {
            null
        }
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}