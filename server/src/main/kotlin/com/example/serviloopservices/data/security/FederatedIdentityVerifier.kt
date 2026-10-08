package com.example.serviloopservices.data.security

/** Identidad resuelta a partir de un id token de un proveedor externo. */
data class FederatedIdentity(
    val uid: String,
    val email: String,
    val name: String?,
)

/**
 * Verifica el `idToken` de un proveedor federado (p. ej. Firebase) y devuelve
 * la identidad asociada, o `null` si el token no es válido.
 *
 * La implementación real (Firebase Admin) se inyecta desde `Application.module()`.
 * Por defecto el login federado queda deshabilitado hasta configurar credenciales.
 */
interface FederatedIdentityVerifier {
    fun verify(idToken: String): FederatedIdentity?
}

/** Implementación por defecto: rechaza cualquier token (login federado apagado). */
class DisabledFederatedIdentityVerifier : FederatedIdentityVerifier {
    override fun verify(idToken: String): FederatedIdentity? = null
}
