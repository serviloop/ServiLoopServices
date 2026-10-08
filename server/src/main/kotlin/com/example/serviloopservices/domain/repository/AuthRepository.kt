package com.example.serviloopservices.domain.repository

import com.example.serviloopservices.data.mapper.FederatedAuthRequest
import com.example.serviloopservices.data.mapper.LoginRequest
import com.example.serviloopservices.data.mapper.RegisterRequest
import com.example.serviloopservices.data.mapper.UserResponse
import com.example.serviloopservices.domain.usecase.AuthResult

interface AuthRepository {
    fun register(request: RegisterRequest): AuthResult
    fun login(request: LoginRequest): AuthResult
    fun federated(request: FederatedAuthRequest): AuthResult
    fun profile(userId: Int): UserResponse?
    fun refresh(userId: Int): AuthResult
    fun updateDeviceToken(userId: Int, deviceToken: String?): Boolean
}
