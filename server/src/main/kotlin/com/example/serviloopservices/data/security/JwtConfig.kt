package com.example.serviloopservices.data.security

/** Nombre del esquema de autenticación JWT registrado en Ktor. */
const val JWT_AUTH_NAME = "auth-jwt"

/** Tipo de token devuelto en [com.example.serviloopservices.data.mapper.AuthResponse]. */
const val TOKEN_TYPE = "Bearer"

/** Claim que transporta el id del usuario en el JWT. */
const val USER_ID_CLAIM = "userId"

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expirySeconds: Long,
) {
    companion object {
        fun from(env: Map<String, String>): JwtConfig = JwtConfig(
            secret = env["JWT_SECRET"] ?: "dev-insecure-secret-change-me",
            issuer = env["JWT_ISSUER"] ?: "serviloopservices",
            audience = env["JWT_AUDIENCE"] ?: "serviloopservices-client",
            realm = env["JWT_REALM"] ?: "serviloopservices",
            expirySeconds = env["JWT_EXPIRY_SECONDS"]?.toLongOrNull() ?: 3_600L,
        )
    }
}
