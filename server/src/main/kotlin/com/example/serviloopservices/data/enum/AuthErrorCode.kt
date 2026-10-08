package com.example.serviloopservices.data.enum

/**
 * Códigos de error públicos del módulo de autenticación.
 * Se serializan como `{ "error": "<nombre>" }` (ver `AuthErrorCode.toResponse()`).
 */
enum class AuthErrorCode {
    INVALID_EMAIL,
    INVALID_PASSWORD,
    INVALID_NAME,
    INVALID_TOKEN,
    INVALID_PROVIDER,
    INVALID_CREDENTIALS,
    EMAIL_ALREADY_REGISTERED,
    USER_NOT_FOUND,
    UNAUTHORIZED,
}
