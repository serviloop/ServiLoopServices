package com.example.serviloopservices.data.security

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

data class GeneratedImage(
    val data: String,
    val mimeType: String,
)

/** Cliente de Gemini (visión + generación de imagen) usando la API `generateContent`. */
class GeminiClient(
    private val httpClient: HttpClient,
    private val config: GeminiConfig,
) {

    suspend fun describeDesign(imageBase64: String, mimeType: String): String {
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = TryOnPrompts.DESCRIBE_PROMPT),
                        GeminiPart(inlineData = GeminiInlineData(mimeType, imageBase64)),
                    ),
                ),
            ),
        )
        val response = generate(config.visionModel, request) ?: return ""
        return response.candidates.firstOrNull()?.content?.parts
            ?.mapNotNull { it.text }
            ?.joinToString(" ")
            ?.trim()
            .orEmpty()
    }

    suspend fun generateImage(
        instruction: String,
        handBase64: String,
        handMime: String,
        designBase64: String,
        designMime: String,
    ): GeneratedImage? {
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = instruction),
                        GeminiPart(inlineData = GeminiInlineData(handMime, handBase64)),
                        GeminiPart(inlineData = GeminiInlineData(designMime, designBase64)),
                    ),
                ),
            ),
            generationConfig = GeminiGenerationConfig(
                responseModalities = listOf("TEXT", "IMAGE"),
                temperature = 0.2,
            ),
        )
        val response = generate(config.imageModel, request) ?: return null
        val parts = response.candidates.firstOrNull()?.content?.parts.orEmpty()
        val inline = parts.firstOrNull { !it.inlineData?.data.isNullOrEmpty() }?.inlineData ?: return null
        return GeneratedImage(data = inline.data, mimeType = inline.mimeType)
    }

    private suspend fun generate(model: String, request: GeminiRequest): GeminiResponse? {
        val response = httpClient.post(
            "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent",
        ) {
            header("x-goog-api-key", config.apiKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) return null
        return response.body()
    }
}

@Serializable
private data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
)

@Serializable
private data class GeminiContent(val parts: List<GeminiPart>)

@Serializable
private data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null,
)

@Serializable
private data class GeminiInlineData(
    val mimeType: String,
    val data: String,
)

@Serializable
private data class GeminiGenerationConfig(
    val responseModalities: List<String>,
    val temperature: Double? = null,
)

@Serializable
private data class GeminiCandidate(val content: GeminiContent? = null)

@Serializable
private data class GeminiResponse(val candidates: List<GeminiCandidate> = emptyList())
