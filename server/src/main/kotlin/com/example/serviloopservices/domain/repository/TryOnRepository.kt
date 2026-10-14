package com.example.serviloopservices.domain.repository

import com.example.serviloopservices.data.mapper.DesignResponse
import com.example.serviloopservices.data.mapper.TryOnRequest
import com.example.serviloopservices.data.mapper.UploadDesignRequest
import com.example.serviloopservices.domain.usecase.DesignResult
import com.example.serviloopservices.domain.usecase.TryOnResult

interface TryOnRepository {
    suspend fun listDesigns(userId: Int): List<DesignResponse>
    suspend fun uploadDesign(userId: Int, request: UploadDesignRequest): DesignResult
    suspend fun deleteDesign(userId: Int, designId: Int): Boolean
    suspend fun generate(userId: Int, request: TryOnRequest): TryOnResult
}
