package com.sloth.ScreenWatcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sloth.ScreenWatcher.databinding.ActivityScreenBinding
import com.sloth.ScreenWatcher.ui.presentation.AuthActivity

class ScreenActivity : AppCompatActivity() {

    lateinit var binding : ActivityScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen)

        binding = ActivityScreenBinding.inflate(layoutInflater)
        // Define a view raiz usando o binding
        setContentView(binding.root)

        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }

        binding.startButton.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }
}