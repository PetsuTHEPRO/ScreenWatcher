package com.sloth.ScreenWatcher.ui.connections

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.sloth.ScreenWatcher.MainActivity
import com.sloth.ScreenWatcher.R
import com.sloth.ScreenWatcher.ScreenApplication
import com.sloth.ScreenWatcher.auth.domain.model.MyConnection
import com.sloth.ScreenWatcher.databinding.ActivityConnectionBinding
import com.sloth.ScreenWatcher.ui.presentation.AuthActivity

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

        // Observa se clickou na primeira connexao
        binding.connection1Image.setOnClickListener {
            connectionViewModel.setConnectionId(connectionViewModel.connection1.value?.connectionId)
            val value: String? = connectionViewModel.getConnectionId()

            Log.i("SCREEN_WATCHER", "Connection ID: ${value}")
            navigateToMainActivity()
        }

        // Observa se clickou na primeira connexao
        binding.connection2Image.setOnClickListener {
            connectionViewModel.setConnectionId(connectionViewModel.connection2.value?.connectionId)
            val value: String? = connectionViewModel.getConnectionId()

            Log.i("SCREEN_WATCHER", "Connection ID: ${value}")
        }

        // Observa se clickou na primeira connexao
        binding.connection3Image.setOnClickListener {
            connectionViewModel.setConnectionId(connectionViewModel.connection3.value?.connectionId)
            val value: String? = connectionViewModel.getConnectionId()

            Log.i("SCREEN_WATCHER", "Connection ID: ${value}")
        }

        // Observa se clickou na primeira connexao
        binding.connection4Image.setOnClickListener {
            connectionViewModel.setConnectionId(connectionViewModel.connection4.value?.connectionId)
            val value: String? = connectionViewModel.getConnectionId()

            Log.i("SCREEN_WATCHER", "Connection ID: ${value}")
        }
        // Carrega as conexões
        val currentUser = connectionViewModel.getCurrentUser()

        if (currentUser != null) {
            connectionViewModel.loadConnections(currentUser)
        }

        // Botão para adicionar nova conexão
        val addConnectionButton = binding.addConnectionButton
        addConnectionButton.setOnClickListener { showAddConnectionDialog() }

        val logoutButton = binding.logout
        logoutButton.setOnClickListener {
            connectionViewModel.logout()
            intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showAddConnectionDialog() {
        val editText = EditText(this).apply { hint = "ex: usuario123" }

        AlertDialog.Builder(this)
            .setTitle("Nova Conexão")
            .setView(editText)
            .setPositiveButton("Conectar") { _, _ ->
                val targetUser = editText.text.toString()
                if (targetUser.isNotBlank()) {

                    val currentUser = connectionViewModel.getCurrentUser()

                    if (currentUser != null) {
                        Log.i("SCREEN_WATCHER", "Target user: ${currentUser}")
                        connectionViewModel.createConnection(currentUser.toString(), targetUser)
                    }
                } else {
                    Toast.makeText(this, "Username não pode ser vazio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateConnectionUI(position: Int, connection: MyConnection?) {
        val (imageView, textView) = when (position) {
            1 -> binding.connection1Image to binding.connection1Text
            2 -> binding.connection2Image to binding.connection2Text
            3 -> binding.connection3Image to binding.connection3Text
            4 -> binding.connection4Image to binding.connection4Text
            else -> return
        }

        if (connection != null) {
            val otherUser = if (connection.connectionId != connectionViewModel.getCurrentUser()) {
                connection.connectionId
            } else {
                connection.targetUser // ou outro campo para determinar o nome do outro usuário
            }
            Log.i("SCREEN_WATCHER", "Current user: ${connectionViewModel.getCurrentUser()} / Other user: ${connection.targetUser}")

            // Exibindo o nome do outro usuário no textView
            textView.text = otherUser.replaceFirstChar { it.uppercase() } // Transforma a primeira letra em maiúscula
            textView.setTextColor(Color.WHITE)

            // Configurando o avatar (caso necessário)
            val avatarUrl = "https://api.dicebear.com/9.x/avataaars/png?seed=aiden&backgroundColor=b6e3f4&backgroundType=gradientLinear"
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.no_image)
                .into(imageView)
        } else {
            // Slot vazio
            textView.text = "Conexão #${position}"
            textView.setTextColor(ContextCompat.getColor(this, R.color.gray))

            val avatarDefault = "https://api.dicebear.com/9.x/avataaars/png?seed=Adrian&backgroundColor=393b40&backgroundType=gradientLinear&accessories[]&accessoriesColor[]&clothesColor[]&clothing[]&clothingGraphic[]&eyebrows[]&eyes=xDizzy&facialHair[]&facialHairColor[]&hairColor[]&hatColor[]&mouth=serious&skinColor=6b6b6b&style=default&top[]"
            Glide.with(this)
                .load(avatarDefault)
                .placeholder(R.drawable.no_image)
                .into(imageView)
        }
    }


    private fun showProgress() { /* Mostrar progress bar */ }
    private fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
}