package com.example.serviloopservices.data.enum

import kotlinx.serialization.Serializable

/** Proveedor de identidad usado en el login federado. */
@Serializable
enum class AuthProvider {
    PASSWORD,
    GOOGLE,
    FACEBOOK,
    APPLE,
}
