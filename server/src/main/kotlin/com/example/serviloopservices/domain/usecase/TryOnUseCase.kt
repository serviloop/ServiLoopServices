package com.example.serviloopservices.domain.usecase

import com.example.serviloopservices.data.mapper.DesignResponse
import com.example.serviloopservices.data.mapper.TryOnRequest
import com.example.serviloopservices.data.mapper.UploadDesignRequest
import com.example.serviloopservices.domain.repository.TryOnRepository

/** Orquestación mínima: delega cada operación en el repositorio. */
class TryOnUseCase(private val repository: TryOnRepository) {
    suspend fun listDesigns(userId: Int): List<DesignResponse> = repository.listDesigns(userId)

    suspend fun uploadDesign(userId: Int, request: UploadDesignRequest): DesignResult =
        repository.uploadDesign(userId, request)

    suspend fun deleteDesign(userId: Int, designId: Int): Boolean =
        repository.deleteDesign(userId, designId)

    suspend fun generate(userId: Int, request: TryOnRequest): TryOnResult =
        repository.generate(userId, request)
}
