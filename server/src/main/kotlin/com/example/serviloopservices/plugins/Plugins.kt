package com.example.serviloopservices.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.serviloopservices.data.enum.AuthErrorCode
import com.example.serviloopservices.data.mapper.toResponse
import com.example.serviloopservices.data.security.JWT_AUTH_NAME
import com.example.serviloopservices.data.security.JwtConfig
import com.example.serviloopservices.data.security.USER_ID_CLAIM
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
        )
    }
}

fun Application.configureAuth(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt(JWT_AUTH_NAME) {
            realm = jwtConfig.realm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                    .withIssuer(jwtConfig.issuer)
                    .withAudience(jwtConfig.audience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(USER_ID_CLAIM).asLong() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, AuthErrorCode.UNAUTHORIZED.toResponse())
            }
        }
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.application.log.debug("Bad request", cause)
            call.respond(HttpStatusCode.BadRequest, AuthErrorCode.INVALID_TOKEN.toResponse())
        }
        exception<ContentTransformationException> { call, cause ->
            call.application.log.debug("Invalid body", cause)
            call.respond(HttpStatusCode.BadRequest, AuthErrorCode.INVALID_TOKEN.toResponse())
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled error", cause)
            call.respond(HttpStatusCode.InternalServerError, AuthErrorCode.UNAUTHORIZED.toResponse())
        }
    }
}
