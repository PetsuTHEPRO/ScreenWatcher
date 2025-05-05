package com.sloth.ScreenWatcher

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.data.datasource.ConnectionRemoteDataSource
import com.sloth.ScreenWatcher.auth.data.repository.AuthRepositoryImpl
import com.sloth.ScreenWatcher.auth.data.repository.ConnectionRepositoryImpl
import com.sloth.ScreenWatcher.auth.domain.repository.AuthRepository
import com.sloth.ScreenWatcher.auth.domain.repository.ConnectionRepository

class ScreenApplication : Application() {

    lateinit var authRepository: AuthRepository
    lateinit var connectionRepository: ConnectionRepository

    override fun onCreate() {
        super.onCreate()

        Log.d("SCREEN_WATCHER", "MyApp inicializado")

        val firestore = FirebaseFirestore.getInstance()
        val realtimeDb = Firebase.database.reference

        val authRemoteDataSource = AuthRemoteDataSource(firestore, realtimeDb)
        authRepository = AuthRepositoryImpl(authRemoteDataSource)

        val connectionRemoteDataSource = ConnectionRemoteDataSource(realtimeDb)
        connectionRepository = ConnectionRepositoryImpl(connectionRemoteDataSource)

        // Você pode colocar mais coisas globais aqui, se quiser depois
    }
}