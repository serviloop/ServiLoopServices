package com.example.serviloopservices.data.database.design

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class DesignDao {

    fun create(
        storeId: Int?,
        name: String,
        prompt: String,
        imageBase64: String,
        mimeType: String,
    ): DesignEntity = transaction {
        val createdAt = System.currentTimeMillis()
        val id = Designs.insert {
            it[Designs.storeId] = storeId
            it[Designs.name] = name
            it[Designs.prompt] = prompt
            it[Designs.imageBase64] = imageBase64
            it[Designs.mimeType] = mimeType
            it[Designs.createdAt] = createdAt
        } get Designs.id
        DesignEntity(id, storeId, name, prompt, imageBase64, mimeType, createdAt)
    }

    fun listByStore(storeId: Int?): List<DesignEntity> = transaction {
        val rows = if (storeId == null) {
            Designs.selectAll().where { Designs.storeId.isNull() }
        } else {
            Designs.selectAll().where { Designs.storeId eq storeId }
        }
        rows.map { it.toEntity() }
    }

    fun findById(id: Int): DesignEntity? = transaction {
        Designs.selectAll().where { Designs.id eq id }.singleOrNull()?.toEntity()
    }

    fun delete(id: Int, storeId: Int?): Boolean = transaction {
        val existing = Designs.selectAll().where { Designs.id eq id }.singleOrNull()?.toEntity()
            ?: return@transaction false
        if (existing.storeId != storeId) return@transaction false
        Designs.deleteWhere { with(it) { Designs.id eq id } } > 0
    }
}

private fun ResultRow.toEntity(): DesignEntity = DesignEntity(
    id = this[Designs.id],
    storeId = this[Designs.storeId],
    name = this[Designs.name],
    prompt = this[Designs.prompt],
    imageBase64 = this[Designs.imageBase64],
    mimeType = this[Designs.mimeType],
    createdAt = this[Designs.createdAt],
)
