package com.example.serviloopservices.domain.usecase

import com.example.serviloopservices.data.mapper.FederatedAuthRequest
import com.example.serviloopservices.data.mapper.LoginRequest
import com.example.serviloopservices.data.mapper.RegisterRequest
import com.example.serviloopservices.data.mapper.UserResponse
import com.example.serviloopservices.domain.repository.AuthRepository

/** Orquestación mínima: delega cada operación en el repositorio. */
class AuthUseCase(private val repository: AuthRepository) {
    fun register(request: RegisterRequest): AuthResult = repository.register(request)

    fun login(request: LoginRequest): AuthResult = repository.login(request)

    fun federated(request: FederatedAuthRequest): AuthResult = repository.federated(request)

    fun profile(userId: Int): UserResponse? = repository.profile(userId)

    fun refresh(userId: Int): AuthResult = repository.refresh(userId)

    fun updateDeviceToken(userId: Int, deviceToken: String?): Boolean =
        repository.updateDeviceToken(userId, deviceToken)
}
