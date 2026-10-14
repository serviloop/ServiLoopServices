package com.example.serviloopservices.data.security

/** Configuración de Gemini leída de variables de entorno. */
data class GeminiConfig(
    val apiKey: String,
    val imageModel: String,
    val visionModel: String,
) {
    companion object {
        fun from(env: Map<String, String>): GeminiConfig = GeminiConfig(
            apiKey = env["GEMINI_API_KEY"].orEmpty(),
            imageModel = env["GEMINI_IMAGE_MODEL"] ?: "gemini-2.5-flash-image",
            visionModel = env["GEMINI_VISION_MODEL"] ?: "gemini-3.8-flash",
        )
    }
}
