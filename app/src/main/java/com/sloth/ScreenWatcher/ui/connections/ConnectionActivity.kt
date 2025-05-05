package com.sloth.ScreenWatcher.ui.connections

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ScreenWatcher.ui.login.LoginFragment
import com.example.ScreenWatcher.ui.login.LoginViewModel
import com.sloth.ScreenWatcher.R
import com.sloth.ScreenWatcher.ScreenApplication
import com.sloth.ScreenWatcher.auth.data.session.SessionManager
import com.sloth.ScreenWatcher.auth.domain.model.Connection
import com.sloth.ScreenWatcher.databinding.ActivityConnectionBinding
import com.sloth.ScreenWatcher.ui.presentation.login.LoginViewModelFactory

class ConnectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionBinding
    private lateinit var connectionViewModel: ConnectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialize o FirebaseAuth e FirebaseFirestore
        val app = application as ScreenApplication
        val factory = ConnectionViewModelFactory(app.connectionRepository)

        // Instancie o ViewModel
        connectionViewModel = ViewModelProvider(this, factory)[ConnectionViewModel::class.java]

        // Observa o estado geral (opcional para loading/erros)
        connectionViewModel.connectionListState.observe(this) { state ->
            when (state) {
                is ConnectionViewModel.ConnectionListState.Loading -> showProgress()
                is ConnectionViewModel.ConnectionListState.Error -> showError(state.message)
                else -> {}
            }
        }

        // Observa cada conexão individualmente
        connectionViewModel.connection1.observe(this) { updateConnectionUI(1, it) }
        connectionViewModel.connection2.observe(this) { updateConnectionUI(2, it) }
        connectionViewModel.connection3.observe(this) { updateConnectionUI(3, it) }
        connectionViewModel.connection4.observe(this) { updateConnectionUI(4, it) }

        // Carrega as conexões
        val currentUser = "usuario_logado" // Substitua pela lógica real
        connectionViewModel.loadConnections(currentUser)

        // Botão para adicionar nova conexão
        val addConnectionButton = findViewById<Button>(R.id.addConnectionButton)
        addConnectionButton.setOnClickListener { showAddConnectionDialog() }
    }

    private fun showAddConnectionDialog() {
        val editText = EditText(this).apply { hint = "ex: usuario123" }

        AlertDialog.Builder(this)
            .setTitle("Nova Conexão")
            .setView(editText)
            .setPositiveButton("Conectar") { _, _ ->
                val targetUser = editText.text.toString()
                if (targetUser.isNotBlank()) {
                    val currentUser = SessionManager(this).getCurrentUser()

                    if (currentUser != null) {
                        connectionViewModel.createConnection(currentUser.username, targetUser)
                    }
                } else {
                    Toast.makeText(this, "Username não pode ser vazio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateConnectionUI(position: Int, connection: Connection?) {
        val (imageView, textView) = when (position) {
            1 -> binding.connection1Image to binding.connection1Text
            2 -> binding.connection2Image to binding.connection2Text
            3 -> binding.connection3Image to binding.connection3Text
            4 -> binding.connection4Image to binding.connection4Text
            else -> return
        }

        // Gera URL do avatar com DiceBear
        val avatarUrl = "https://api.dicebear.com/8.x/identicon/png?seed=${connection?.user1}&size=150"

        if (connection != null) {
            // Conexão ativa
            textView.text = connection.user2
            textView.setTextColor(Color.WHITE)
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.no_image)
                .into(imageView)
        } else {
            // Slot vazio
            textView.text = "Conexão #$position"
            textView.setTextColor(ContextCompat.getColor(this, R.color.black))
            imageView.setImageResource(R.drawable.no_image)
        }
    }

    private fun showProgress() { /* Mostrar progress bar */ }
    private fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
}