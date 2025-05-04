package com.sloth.ScreenWatcher.ui.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ScreenWatcher.ui.login.LoginFragment
import com.sloth.ScreenWatcher.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login1) // Melhor nome se for genérico

        // Carrega o LoginFragment apenas na primeira criação
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment())
                .commit()
        }
    }
}