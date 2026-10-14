package com.example.serviloopservices.routes

import com.example.serviloopservices.data.enum.AuthErrorCode
import com.example.serviloopservices.data.enum.TryOnErrorCode
import com.example.serviloopservices.data.mapper.DesignListResponse
import com.example.serviloopservices.data.mapper.TryOnRequest
import com.example.serviloopservices.data.mapper.TryOnResponse
import com.example.serviloopservices.data.mapper.UploadDesignRequest
import com.example.serviloopservices.data.mapper.UploadDesignResponse
import com.example.serviloopservices.data.mapper.toResponse
import com.example.serviloopservices.data.security.JWT_AUTH_NAME
import com.example.serviloopservices.domain.usecase.DesignResult
import com.example.serviloopservices.domain.usecase.TryOnResult
import com.example.serviloopservices.domain.usecase.TryOnUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.tryOnRoutes(useCase: TryOnUseCase) {
    authenticate(JWT_AUTH_NAME) {
        route("/tryon") {
            post {
                val userId = call.requireUserId() ?: return@post
                when (val result = useCase.generate(userId, call.receive<TryOnRequest>())) {
                    is TryOnResult.Success -> call.respond(
                        HttpStatusCode.OK,
                        TryOnResponse(
                            image = result.image,
                            imageMimeType = result.imageMimeType,
                            model = result.model,
                            processingMs = result.processingMs,
                        ),
                    )

                    is TryOnResult.InvalidInput ->
                        call.respond(HttpStatusCode.BadRequest, result.code.toResponse())

                    TryOnResult.DesignNotFound ->
                        call.respond(HttpStatusCode.NotFound, TryOnErrorCode.DESIGN_NOT_FOUND.toResponse())

                    TryOnResult.GenerationFailed ->
                        call.respond(HttpStatusCode.BadGateway, TryOnErrorCode.GENERATION_FAILED.toResponse())
                }
            }
        }

        route("/designs") {
            get {
                val userId = call.requireUserId() ?: return@get
                call.respond(HttpStatusCode.OK, DesignListResponse(useCase.listDesigns(userId)))
            }

            post {
                val userId = call.requireUserId() ?: return@post
                when (val result = useCase.uploadDesign(userId, call.receive<UploadDesignRequest>())) {
                    is DesignResult.Uploaded -> call.respond(
                        HttpStatusCode.Created,
                        UploadDesignResponse(id = result.id, autoPrompt = result.autoPrompt),
                    )

                    is DesignResult.InvalidInput ->
                        call.respond(HttpStatusCode.BadRequest, result.code.toResponse())
                }
            }

            delete("/{id}") {
                val userId = call.requireUserId() ?: return@delete
                val designId = call.parameters["id"]?.toIntOrNull()
                if (designId == null) {
                    call.respond(HttpStatusCode.BadRequest, TryOnErrorCode.INVALID_DESIGN.toResponse())
                    return@delete
                }
                if (useCase.deleteDesign(userId, designId)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, TryOnErrorCode.DESIGN_NOT_FOUND.toResponse())
                }
            }
        }
    }
}

private suspend fun ApplicationCall.requireUserId(): Int? {
    val userId = userId()
    if (userId == null) {
        respond(HttpStatusCode.Unauthorized, AuthErrorCode.UNAUTHORIZED.toResponse())
    }
    return userId
}
