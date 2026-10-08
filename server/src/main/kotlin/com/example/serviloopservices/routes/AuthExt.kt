package com.example.serviloopservices.routes

import com.example.serviloopservices.data.security.USER_ID_CLAIM
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

/** Id del usuario autenticado extraído del JWT, o `null` si no hay sesión válida. */
fun ApplicationCall.userId(): Int? =
    principal<JWTPrincipal>()
        ?.payload
        ?.getClaim(USER_ID_CLAIM)
        ?.asLong()
        ?.toInt()
