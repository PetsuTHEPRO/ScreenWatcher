package com.sloth.ScreenWatcher

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class ScreenApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("SCREEN_WATCHER", "MyApp inicializado")

        // Inicializa o Firebase aqui
        FirebaseApp.initializeApp(this)

        // Você pode colocar mais coisas globais aqui, se quiser depois
    }
}