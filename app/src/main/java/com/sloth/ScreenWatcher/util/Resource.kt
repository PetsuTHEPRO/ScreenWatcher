package com.sloth.ScreenWatcher.util

/**
 * Encapsula os possíveis estados de uma operação assíncrona
 *
 * @param T Tipo do dado encapsulado
 */
sealed class Resource<out T> {
    /**
     * Representa um estado de carregamento
     * @param partialData Dados parciais que podem ser mostrados durante o loading
     */
    data class Loading<T>(val partialData: T? = null) : Resource<T>()

    /**
     * Representa um estado de sucesso
     * @param data Dado retornado pela operação
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Representa um estado de erro
     * @param error Mensagem de erro amigável
     * @param throwable Exceção original (opcional)
     * @param errorCode Código de erro customizado (opcional)
     */
    data class Error(
        val error: String,
        val throwable: Throwable? = null,
        val errorCode: Int? = null
    ) : Resource<Nothing>()

    /**
     * Extensão para transformar o dado quando em estado Success
     */
    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Loading -> Loading(partialData?.let(transform))
            is Success -> Success(transform(data))
            is Error -> this
        }
    }

    companion object {
        /**
         * Helper para criar um Resource.Success
         */
        fun <T> success(data: T) = Success(data)

        /**
         * Helper para criar um Resource.Error
         */
        fun error(message: String, throwable: Throwable? = null) = Error(message, throwable)
    }
}

/**
 * Extensão para operações com corrotinas
 */
suspend fun <T> Resource<T>.onSuccess(
    action: suspend (T) -> Unit
): Resource<T> {
    if (this is Resource.Success) {
        action(data)
    }
    return this
}

/**
 * Extensão para tratamento de erro simplificado
 */
suspend fun <T> Resource<T>.onError(
    action: suspend (message: String, throwable: Throwable?) -> Unit
): Resource<T> {
    if (this is Resource.Error) {
        action(error, throwable)
    }
    return this
}