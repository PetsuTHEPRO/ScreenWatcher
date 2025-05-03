package com.sloth.ScreenWatcher.auth.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): AuthUser {
        try {
            // 1. Autenticação com Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()

            // 2. Obter dados do usuário do Firestore
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            return getUserFromFirestore(userId)

        } catch (e: Exception) {
            throw when (e.message) {
                "The email address is badly formatted." -> Exception("E-mail inválido")
                "The password is invalid or the user does not have a password." -> Exception("Senha incorreta")
                "There is no user record corresponding to this identifier." -> Exception("Usuário não encontrado")
                else -> Exception("Falha no login: ${e.message ?: "Erro desconhecido"}")
            }
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