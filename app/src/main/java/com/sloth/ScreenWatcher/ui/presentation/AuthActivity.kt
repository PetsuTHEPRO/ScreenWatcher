package com.sloth.ScreenWatcher.ui.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sloth.ScreenWatcher.R
import com.sloth.ScreenWatcher.ui.presentation.login.MainLogin

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login1)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainLogin.newInstance())
                .commitNow()
        }
    }
}