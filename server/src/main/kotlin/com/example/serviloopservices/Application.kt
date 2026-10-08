package com.example.serviloopservices

import com.example.serviloopservices.data.database.DatabaseConfig
import com.example.serviloopservices.data.database.DatabaseFactory
import com.example.serviloopservices.data.database.user.UserDao
import com.example.serviloopservices.data.repositoryImpl.AuthRepositoryImpl
import com.example.serviloopservices.data.security.DisabledFederatedIdentityVerifier
import com.example.serviloopservices.data.security.JwtConfig
import com.example.serviloopservices.data.security.PasswordHasher
import com.example.serviloopservices.data.security.TokenService
import com.example.serviloopservices.domain.usecase.AuthUseCase
import com.example.serviloopservices.plugins.configureAuth
import com.example.serviloopservices.plugins.configureSerialization
import com.example.serviloopservices.plugins.configureStatusPages
import com.example.serviloopservices.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val env = System.getenv()

    val jwtConfig = JwtConfig.from(env)
    DatabaseFactory.init(DatabaseConfig.from(env))

    val userDao = UserDao()
    val passwordHasher = PasswordHasher()
    val tokenService = TokenService(jwtConfig)
    val authRepository = AuthRepositoryImpl(
        userDao = userDao,
        passwordHasher = passwordHasher,
        tokenService = tokenService,
        federatedVerifier = DisabledFederatedIdentityVerifier(),
    )
    val authUseCase = AuthUseCase(authRepository)

    configureSerialization()
    configureAuth(jwtConfig)
    configureStatusPages()

    routing {
        get("/") {
            call.respondText(sayHello("Ktor"))
        }
        authRoutes(authUseCase)
    }
}
