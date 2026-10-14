package com.example.serviloopservices.data.mapper

import com.example.serviloopservices.data.database.design.DesignEntity
import com.example.serviloopservices.data.enum.TryOnErrorCode
import kotlinx.serialization.Serializable

@Serializable
data class TryOnRequest(
    /** Base64 de la foto de la mano. */
    val image: String,
    val imageMimeType: String = "image/jpeg",
    /** Id del diseño (pertenece a la tienda del usuario). */
    val designId: Int,
    /** Ajuste opcional del usuario. */
    val prompt: String? = null,
)

@Serializable
data class TryOnResponse(
    val image: String,
    val imageMimeType: String,
    val model: String,
    val processingMs: Long,
)

@Serializable
data class DesignResponse(
    val id: Int,
    val name: String,
    val prompt: String,
    val imageBase64: String,
    val mimeType: String,
)

@Serializable
data class DesignListResponse(
    val designs: List<DesignResponse>,
)

@Serializable
data class UploadDesignRequest(
    val name: String,
    val prompt: String = "",
    val imageBase64: String,
    val mimeType: String = "image/png",
)

@Serializable
data class UploadDesignResponse(
    val id: Int,
    val autoPrompt: String,
)

fun DesignEntity.toResponse(): DesignResponse = DesignResponse(
    id = id,
    name = name,
    prompt = prompt,
    imageBase64 = imageBase64,
    mimeType = mimeType,
)

fun TryOnErrorCode.toResponse(): ErrorResponse = ErrorResponse(name)
