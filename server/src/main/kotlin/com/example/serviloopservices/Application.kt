package com.example.serviloopservices

import com.example.serviloopservices.data.database.DatabaseConfig
import com.example.serviloopservices.data.database.DatabaseFactory
import com.example.serviloopservices.data.database.design.DesignDao
import com.example.serviloopservices.data.database.user.UserDao
import com.example.serviloopservices.data.repositoryImpl.AuthRepositoryImpl
import com.example.serviloopservices.data.repositoryImpl.TryOnRepositoryImpl
import com.example.serviloopservices.data.security.DisabledFederatedIdentityVerifier
import com.example.serviloopservices.data.security.GeminiClient
import com.example.serviloopservices.data.security.GeminiConfig
import com.example.serviloopservices.data.security.JwtConfig
import com.example.serviloopservices.data.security.PasswordHasher
import com.example.serviloopservices.data.security.TokenService
import com.example.serviloopservices.domain.usecase.AuthUseCase
import com.example.serviloopservices.domain.usecase.TryOnUseCase
import com.example.serviloopservices.plugins.configureAuth
import com.example.serviloopservices.plugins.configureSerialization
import com.example.serviloopservices.plugins.configureStatusPages
import com.example.serviloopservices.routes.authRoutes
import com.example.serviloopservices.routes.tryOnRoutes
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

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
    val designDao = DesignDao()
    val passwordHasher = PasswordHasher()
    val tokenService = TokenService(jwtConfig)

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            socketTimeoutMillis = 120_000
            connectTimeoutMillis = 30_000
        }
    }
    val geminiClient = GeminiClient(httpClient, GeminiConfig.from(env))

    val authRepository = AuthRepositoryImpl(
        userDao = userDao,
        passwordHasher = passwordHasher,
        tokenService = tokenService,
        federatedVerifier = DisabledFederatedIdentityVerifier(),
    )
    val authUseCase = AuthUseCase(authRepository)

    val tryOnRepository = TryOnRepositoryImpl(
        userDao = userDao,
        designDao = designDao,
        geminiClient = geminiClient,
    )
    val tryOnUseCase = TryOnUseCase(tryOnRepository)

    configureSerialization()
    configureAuth(jwtConfig)
    configureStatusPages()

    routing {
        get("/") {
            call.respondText(sayHello("Ktor"))
        }
        authRoutes(authUseCase)
        tryOnRoutes(tryOnUseCase)
    }
}
