package com.sloth.ScreenWatcher

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.data.datasource.ConnectionRemoteDataSource
import com.sloth.ScreenWatcher.auth.data.datasource.PreferencesManager
import com.sloth.ScreenWatcher.auth.data.repository.AuthRepositoryImpl
import com.sloth.ScreenWatcher.auth.data.repository.ConnectionRepositoryImpl
import com.sloth.ScreenWatcher.auth.data.session.SessionManager
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository
import com.sloth.ScreenWatcher.service.ScreenStateService
import com.sloth.ScreenWatcher.ui.connections.ConnectionActivity
import com.sloth.ScreenWatcher.ui.presentation.AuthActivity

class ScreenApplication : Application(), Application.ActivityLifecycleCallbacks  {

    lateinit var authRepository: AuthRepository
    lateinit var connectionRepository: ConnectionRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        Log.d("SCREEN_WATCHER", "MyApp inicializado")

        val preferencesManager = PreferencesManager(this);
        sessionManager = SessionManager(preferencesManager);
        val realtimeDb = Firebase.database.reference

        val authRemoteDataSource = AuthRemoteDataSource(sessionManager, realtimeDb);
        authRepository = AuthRepositoryImpl(authRemoteDataSource)

        val connectionRemoteDataSource = ConnectionRemoteDataSource(sessionManager, realtimeDb)
        connectionRepository = ConnectionRepositoryImpl(connectionRemoteDataSource)

        // Você pode colocar mais coisas globais aqui, se quiser depois
        startScreenStateService()
    }

    private fun startScreenStateService() {
        val serviceIntent = Intent(this, ScreenStateService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.i("SCREEN_WATCHER", sessionManager.getUsername().toString() + " " + sessionManager.isLoggedIn().toString())

        // Verifica se é a tela de login E se está logado
        if (activity is AuthActivity && sessionManager.isLoggedIn()) {
            activity.startActivity(Intent(activity, ConnectionActivity::class.java))
            activity.finish()
        }
    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }
}