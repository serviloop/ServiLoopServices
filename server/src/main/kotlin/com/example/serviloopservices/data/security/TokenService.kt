package com.example.serviloopservices.data.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

data class IssuedToken(
    val token: String,
    val expiresIn: Long,
)

/** Emite y valida los JWT firmados con HMAC256 que consume la app cliente. */
class TokenService(private val config: JwtConfig) {
    private val algorithm: Algorithm = Algorithm.HMAC256(config.secret)

    fun issue(userId: Int): IssuedToken {
        val expiresIn = config.expirySeconds
        val token = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(USER_ID_CLAIM, userId.toLong())
            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn * 1000))
            .sign(algorithm)
        return IssuedToken(token, expiresIn)
    }

    fun userIdFromToken(token: String): Int? = runCatching {
        JWT.require(algorithm)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()
            .verify(token)
            .getClaim(USER_ID_CLAIM)
            .asLong()
            ?.toInt()
    }.getOrNull()
}
