package com.sloth.ScreenWatcher.auth.data.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSource(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val realtimeDb: DatabaseReference // Adicione esta linha
) {
    suspend fun login(email: String, password: String): AuthUser {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            return getUserFromFirestore(userId)
        } catch (e: Exception) {
            when (e.message) {
                "The email address is badly formatted." -> Log.i("SCREEN_WATCHER", "E-mail inválido")
                "The password is invalid or the user does not have a password." -> Log.i("SCREEN_WATCHER", "Senha incorreta")
                "There is no user record corresponding to this identifier." -> Log.i("SCREEN_WATCHER", "Usuário não encontrado")
                else -> Log.i("SCREEN_WATCHER", "Falha no login: ${e.message ?: "Erro desconhecido"}")
            }
            return AuthUser()
        }
    }

    suspend fun register(email: String, password: String, username: String): Result<Unit> {
        return try {
            val database = Firebase.database
            val usersRef = database.getReference("users")

            val userData = mapOf(
                "username" to username,
                "password" to password,  // você se vira com isso
                "email" to email,
                "createdAt" to System.currentTimeMillis()
            )

            usersRef.child(username).setValue(userData).await() // Espera com coroutine

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getUserFromFirestore(userId: String): AuthUser {
        return db.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject(AuthUser::class.java)
            ?: throw Exception("Dados do usuário não encontrados")
    }
}