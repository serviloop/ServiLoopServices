package com.example.serviloopservices.domain.usecase

import com.example.serviloopservices.data.enum.TryOnErrorCode

/** Resultado de generar un Try-On. */
sealed interface TryOnResult {
    data class Success(
        val image: String,
        val imageMimeType: String,
        val model: String,
        val processingMs: Long,
    ) : TryOnResult

    data class InvalidInput(val code: TryOnErrorCode) : TryOnResult

    data object DesignNotFound : TryOnResult

    data object GenerationFailed : TryOnResult
}

/** Resultado de subir un diseño. */
sealed interface DesignResult {
    data class Uploaded(val id: Int, val autoPrompt: String) : DesignResult

    data class InvalidInput(val code: TryOnErrorCode) : DesignResult
}
