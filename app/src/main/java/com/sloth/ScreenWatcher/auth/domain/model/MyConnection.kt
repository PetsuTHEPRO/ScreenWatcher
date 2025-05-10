package com.sloth.ScreenWatcher.auth.domain.model

data class MyConnection(
    val connectionId: String = "",
    val status: String = "PENDING",
    val createdAt: Long = 0L,
    val targetUser: String = "" // Adicione se necessário
) {
    // Construtor vazio necessário para o Firebase
    constructor() : this("", "PENDING", 0L, "")
}