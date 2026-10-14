package com.example.serviloopservices.data.repositoryImpl

import com.example.serviloopservices.data.database.design.DesignDao
import com.example.serviloopservices.data.database.user.UserDao
import com.example.serviloopservices.data.enum.TryOnErrorCode
import com.example.serviloopservices.data.mapper.DesignResponse
import com.example.serviloopservices.data.mapper.TryOnRequest
import com.example.serviloopservices.data.mapper.UploadDesignRequest
import com.example.serviloopservices.data.mapper.toResponse
import com.example.serviloopservices.data.security.GeminiClient
import com.example.serviloopservices.data.security.TryOnPrompts
import com.example.serviloopservices.domain.repository.TryOnRepository
import com.example.serviloopservices.domain.usecase.DesignResult
import com.example.serviloopservices.domain.usecase.TryOnResult

private const val MAX_IMAGE_BASE64_LENGTH = 8 * 1024 * 1024

/**
 * Reglas del Try-On: validación, acceso a diseños **scoped por la tienda del usuario**
 * e integración con Gemini.
 */
class TryOnRepositoryImpl(
    private val userDao: UserDao,
    private val designDao: DesignDao,
    private val geminiClient: GeminiClient,
) : TryOnRepository {

    override suspend fun listDesigns(userId: Int): List<DesignResponse> =
        designDao.listByStore(storeIdOf(userId)).map { it.toResponse() }

    override suspend fun uploadDesign(userId: Int, request: UploadDesignRequest): DesignResult {
        val name = request.name.trim()
        if (name.isBlank()) return DesignResult.InvalidInput(TryOnErrorCode.INVALID_DESIGN)
        if (request.imageBase64.isBlank() || request.imageBase64.length > MAX_IMAGE_BASE64_LENGTH) {
            return DesignResult.InvalidInput(TryOnErrorCode.INVALID_IMAGE)
        }

        val autoPrompt = geminiClient.describeDesign(request.imageBase64, request.mimeType)
        val combinedPrompt = listOf(autoPrompt, request.prompt.trim(), name)
            .filter { it.isNotBlank() }
            .joinToString("\n")

        val entity = designDao.create(
            storeId = storeIdOf(userId),
            name = name,
            prompt = combinedPrompt,
            imageBase64 = request.imageBase64,
            mimeType = request.mimeType,
        )
        return DesignResult.Uploaded(id = entity.id, autoPrompt = autoPrompt)
    }

    override suspend fun deleteDesign(userId: Int, designId: Int): Boolean =
        designDao.delete(designId, storeIdOf(userId))

    override suspend fun generate(userId: Int, request: TryOnRequest): TryOnResult {
        if (request.image.isBlank() || request.image.length > MAX_IMAGE_BASE64_LENGTH) {
            return TryOnResult.InvalidInput(TryOnErrorCode.INVALID_IMAGE)
        }

        val storeId = storeIdOf(userId)
        val design = designDao.findById(request.designId) ?: return TryOnResult.DesignNotFound
        if (design.storeId != storeId) return TryOnResult.DesignNotFound

        val instruction = if (request.prompt.isNullOrBlank()) {
            TryOnPrompts.INSTRUCTION
        } else {
            "${TryOnPrompts.INSTRUCTION}\n\nUser adjustment: ${request.prompt}"
        }

        val started = System.currentTimeMillis()
        val generated = geminiClient.generateImage(
            instruction = instruction,
            handBase64 = request.image,
            handMime = request.imageMimeType,
            designBase64 = design.imageBase64,
            designMime = design.mimeType,
        ) ?: return TryOnResult.GenerationFailed

        return TryOnResult.Success(
            image = generated.data,
            imageMimeType = generated.mimeType,
            model = "gemini",
            processingMs = System.currentTimeMillis() - started,
        )
    }

    private fun storeIdOf(userId: Int): Int? = userDao.findById(userId)?.storeId
}
