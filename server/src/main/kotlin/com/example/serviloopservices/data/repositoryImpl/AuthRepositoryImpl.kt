package com.example.serviloopservices.data.repositoryImpl

import com.example.serviloopservices.data.database.user.UserDao
import com.example.serviloopservices.data.database.user.UserEntity
import com.example.serviloopservices.data.enum.AuthErrorCode
import com.example.serviloopservices.data.mapper.FederatedAuthRequest
import com.example.serviloopservices.data.mapper.LoginRequest
import com.example.serviloopservices.data.mapper.RegisterRequest
import com.example.serviloopservices.data.mapper.UserResponse
import com.example.serviloopservices.data.mapper.toResponse
import com.example.serviloopservices.data.security.FederatedIdentityVerifier
import com.example.serviloopservices.data.security.PasswordHasher
import com.example.serviloopservices.data.security.TokenService
import com.example.serviloopservices.domain.repository.AuthRepository
import com.example.serviloopservices.domain.usecase.AuthResult
import java.util.UUID

private const val MIN_PASSWORD_LENGTH = 8
private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

/**
 * Contiene las reglas de autenticación: validación, conflictos, hashing,
 * emisión de JWT y ensamblado de la respuesta.
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService,
    private val federatedVerifier: FederatedIdentityVerifier,
) : AuthRepository {

    override fun register(request: RegisterRequest): AuthResult {
        val email = request.email.trim().lowercase()
        val name = request.name.trim()
        validateEmail(email)?.let { return it }
        validatePassword(request.password)?.let { return it }
        if (name.isBlank()) return AuthResult.InvalidInput(AuthErrorCode.INVALID_NAME)
        if (userDao.existsByEmail(email)) return AuthResult.EmailAlreadyRegistered

        val user = userDao.create(email, name, passwordHasher.hash(request.password))
        updateDeviceToken(user.id, request.deviceToken)
        return success(user)
    }

    override fun login(request: LoginRequest): AuthResult {
        val email = request.email.trim().lowercase()
        val user = userDao.findByEmail(email) ?: return AuthResult.InvalidCredentials
        if (!passwordHasher.verify(request.password, user.passwordHash)) {
            return AuthResult.InvalidCredentials
        }
        updateDeviceToken(user.id, request.deviceToken)
        return success(user)
    }

    override fun federated(request: FederatedAuthRequest): AuthResult {
        val idToken = request.idToken?.takeIf { it.isNotBlank() }
            ?: return AuthResult.InvalidInput(AuthErrorCode.INVALID_TOKEN)
        val provider = request.provider
            ?: return AuthResult.InvalidInput(AuthErrorCode.INVALID_PROVIDER)
        val identity = federatedVerifier.verify(idToken) ?: return AuthResult.InvalidToken

        val user = userDao.findByFirebaseUid(identity.uid)
            ?: userDao.findByEmail(identity.email)?.let { existing ->
                userDao.linkFederation(
                    id = existing.id,
                    firebaseUid = identity.uid,
                    authProvider = provider.name,
                    email = identity.email,
                    name = request.name ?: identity.name,
                )
            }
            ?: userDao.createFederated(
                email = identity.email,
                name = request.name ?: identity.name ?: identity.email,
                passwordHash = passwordHasher.hash(UUID.randomUUID().toString()),
                firebaseUid = identity.uid,
                authProvider = provider.name,
            )

        updateDeviceToken(user.id, request.deviceToken)
        return success(user)
    }

    override fun profile(userId: Int): UserResponse? = userDao.findById(userId)?.toResponse()

    override fun refresh(userId: Int): AuthResult {
        val user = userDao.findById(userId) ?: return AuthResult.InvalidToken
        return success(user)
    }

    override fun updateDeviceToken(userId: Int, deviceToken: String?): Boolean {
        val user = userDao.findById(userId) ?: return false
        userDao.updateDeviceToken(user.id, deviceToken?.takeIf { it.isNotBlank() })
        return true
    }

    private fun validateEmail(email: String): AuthResult? =
        if (!EMAIL_REGEX.matches(email)) AuthResult.InvalidInput(AuthErrorCode.INVALID_EMAIL) else null

    private fun validatePassword(password: String): AuthResult? =
        if (password.length < MIN_PASSWORD_LENGTH) {
            AuthResult.InvalidInput(AuthErrorCode.INVALID_PASSWORD)
        } else {
            null
        }

    private fun success(user: UserEntity): AuthResult {
        val issued = tokenService.issue(user.id)
        return AuthResult.Success(
            token = issued.token,
            expiresIn = issued.expiresIn,
            user = user.toResponse(),
        )
    }
}
