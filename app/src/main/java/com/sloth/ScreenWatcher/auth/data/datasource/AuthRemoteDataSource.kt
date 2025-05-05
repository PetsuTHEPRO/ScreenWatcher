package com.sloth.ScreenWatcher.auth.data.datasource

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sloth.ScreenWatcher.auth.domain.model.AuthUser
import com.sloth.ScreenWatcher.auth.domain.model.BasicInfo
import com.sloth.ScreenWatcher.auth.domain.model.UserStatus
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import kotlin.random.Random

class AuthRemoteDataSource(
    private val db: FirebaseFirestore,
    private val realtimeDb: DatabaseReference // Adicione esta linha
) {
    suspend fun login(usernameOrEmail: String, password: String): AuthUser {
        return try {
            // 1. Busca otimizada por email ou username
            val query = if (usernameOrEmail.contains("@")) {
                realtimeDb.child("users")
                    .orderByChild("basicInfo/email")
                    .equalTo(usernameOrEmail)
            } else {
                realtimeDb.child("users")
                    .orderByChild("basicInfo/username")
                    .equalTo(usernameOrEmail)
            }

            val snapshot = query.get().await()

            // 2. Verifica cada resultado
            for (userSnapshot in snapshot.children) {
                val basicInfo = userSnapshot.child("basicInfo").getValue(BasicInfo::class.java)
                val status = userSnapshot.child("status").getValue(UserStatus::class.java)

                if (basicInfo != null &&
                    (basicInfo.username == usernameOrEmail || basicInfo.email == usernameOrEmail) &&
                    verifyPassword(password, basicInfo.passwordHash)) {

                    return AuthUser(
                        id = userSnapshot.key ?: "",
                        basicInfo = basicInfo,
                        status = status ?: UserStatus()
                    )
                }
            }

            throw Exception("Credenciais inválidas")
        } catch (e: Exception) {
            Log.e("SCREEN_WATCHER", "Falha no login", e)
            throw Exception("Falha na autenticação. Tente novamente.")
        }
    }

    suspend fun register(email: String, password: String, username: String): Result<Unit> {
        return try {
            // 1. Verifica se o usuário já existe
            val userRef = realtimeDb.child("users").child(username)
            if (userRef.get().await().exists()) {
                return Result.failure(Exception("Usuário já existe"))
            }

            // 2. Cria hash seguro da senha (nunca armazene em texto puro)
            val hashedPassword = hashPassword(password)

            // 3. Estrutura de dados organizada
            val userData = mapOf(
                "basicInfo" to mapOf(
                    "username" to username,
                    "email" to email,
                    "passwordHash" to hashedPassword,
                    "createdAt" to ServerValue.TIMESTAMP // Usa timestamp do servidor
                ),
                "status" to mapOf(
                    "isOnline" to false,
                    "lastActivity" to ServerValue.TIMESTAMP
                )
            )

            // 4. Salva de forma atômica
            userRef.setValue(userData).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha no registro: ${e.message}"))
        }
    }

    // Função para gerar hash seguro (adicione no mesmo arquivo)
    fun hashPassword(password: String): String {
        val salt = Random.nextBytes(16).toString(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt.toByteArray())
        val hash = digest.digest(password.toByteArray())
        return "$salt:${hash.fold("") { str, it -> str + "%02x".format(it) }}"
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        val (salt, hash) = storedHash.split(":")
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt.toByteArray())
        val newHash = digest.digest(password.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }

        Log.i("SCREEN_WATCHER", "Verificando senha: $password e $hash e $newHash")
        return hash == newHash
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