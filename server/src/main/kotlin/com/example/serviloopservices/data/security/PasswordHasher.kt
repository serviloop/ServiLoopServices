package com.example.serviloopservices.data.security

import org.mindrot.jbcrypt.BCrypt

/** Hashing de contraseñas con BCrypt. */
class PasswordHasher(private val cost: Int = 12) {
    fun hash(raw: String): String = BCrypt.hashpw(raw, BCrypt.gensalt(cost))

    fun verify(raw: String, hash: String): Boolean =
        runCatching { BCrypt.checkpw(raw, hash) }.getOrDefault(false)
}
