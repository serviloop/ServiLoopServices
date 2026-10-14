package com.example.serviloopservices.data.enum

/**
 * Códigos de error públicos del módulo de Try-On.
 * Se serializan como `{ "error": "<nombre>" }`.
 */
enum class TryOnErrorCode {
    INVALID_IMAGE,
    INVALID_DESIGN,
    DESIGN_NOT_FOUND,
    GENERATION_FAILED,
    RATE_LIMITED,
    UNAUTHORIZED,
}
