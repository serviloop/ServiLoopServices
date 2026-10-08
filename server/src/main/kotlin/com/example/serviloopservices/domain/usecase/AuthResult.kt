package com.example.serviloopservices.domain.usecase

import com.example.serviloopservices.data.enum.AuthErrorCode
import com.example.serviloopservices.data.mapper.UserResponse

/** Resultado de una operación de autenticación que emite token + usuario. */
sealed interface AuthResult {
    data class Success(
        val token: String,
        val expiresIn: Long,
        val user: UserResponse,
    ) : AuthResult

    data class InvalidInput(val code: AuthErrorCode) : AuthResult

    data object EmailAlreadyRegistered : AuthResult

    data object InvalidCredentials : AuthResult

    data object InvalidToken : AuthResult
}
