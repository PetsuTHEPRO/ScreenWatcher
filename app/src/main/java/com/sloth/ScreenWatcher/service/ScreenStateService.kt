package com.sloth.ScreenWatcher.service

import android.app.*
import android.content.*
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sloth.ScreenWatcher.R
import com.sloth.ScreenWatcher.ScreenApplication
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository
import kotlinx.coroutines.*

class ScreenStateService : Service() {

    private lateinit var connectionRepository: ConnectionRepository
    private lateinit var screenStateReceiver: BroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        connectionRepository = (application as ScreenApplication).connectionRepository

        registerScreenStateReceiver()  // Registrar o receiver para o estado da tela
        startForegroundNotification()  // Iniciar a notificação em segundo plano
    }

    private fun registerScreenStateReceiver() {
        screenStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val state = when (intent.action) {
                    Intent.ACTION_SCREEN_ON -> "ON"
                    Intent.ACTION_SCREEN_OFF -> "OFF"
                    else -> return
                }

                // Usar CoroutineScope para rodar uma tarefa suspensa fora da main thread
                CoroutineScope(Dispatchers.IO).launch {
                    // Chama o método suspenso para atualizar o estado no Firebase
                    val username = "kate"  // Substitua por uma variável que tenha o usuário logado, se necessário
                    connectionRepository.updateScreenStatusForUser(username, state)
                    Log.d("SCREEN_WATCHER", "Broadcast enviado: $state")
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, filter)  // Registra o receiver para escutar as mudanças
    }

    private fun startForegroundNotification() {
        val channelId = "screen_monitor_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Screen Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitorando tela")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notification)  // Inicia o serviço em primeiro plano
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)  // Remove o receiver quando o serviço for destruído
    }
}