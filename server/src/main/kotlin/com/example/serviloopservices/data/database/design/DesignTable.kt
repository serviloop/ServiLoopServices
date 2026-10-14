package com.example.serviloopservices.data.database.design

import org.jetbrains.exposed.v1.core.Table

const val DESIGN_NAME_MAX_LENGTH = 120
const val DESIGN_MIME_MAX_LENGTH = 100

object Designs : Table("designs") {
    val id = integer("id").autoIncrement()
    val storeId = integer("store_id").nullable()
    val name = varchar("name", DESIGN_NAME_MAX_LENGTH)
    val prompt = text("prompt")
    val imageBase64 = text("image_base64")
    val mimeType = varchar("mime_type", DESIGN_MIME_MAX_LENGTH)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

data class DesignEntity(
    val id: Int,
    val storeId: Int?,
    val name: String,
    val prompt: String,
    val imageBase64: String,
    val mimeType: String,
    val createdAt: Long,
)
