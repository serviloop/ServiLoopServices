package com.example.serviloopservices.data.database.user

import org.jetbrains.exposed.v1.core.Table

const val EMAIL_MAX_LENGTH = 320
const val NAME_MAX_LENGTH = 120
const val PASSWORD_HASH_MAX_LENGTH = 100
const val FIREBASE_UID_MAX_LENGTH = 128
const val AUTH_PROVIDER_MAX_LENGTH = 16
const val DEVICE_TOKEN_MAX_LENGTH = 512

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", EMAIL_MAX_LENGTH).uniqueIndex()
    val name = varchar("name", NAME_MAX_LENGTH)
    val passwordHash = varchar("password_hash", PASSWORD_HASH_MAX_LENGTH)
    val firebaseUid = varchar("firebase_uid", FIREBASE_UID_MAX_LENGTH).nullable()
    val authProvider = varchar("auth_provider", AUTH_PROVIDER_MAX_LENGTH).nullable()
    val storeId = integer("store_id").nullable()
    val deviceToken = varchar("device_token", DEVICE_TOKEN_MAX_LENGTH).nullable()
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

data class UserEntity(
    val id: Int,
    val email: String,
    val name: String,
    val passwordHash: String,
    val firebaseUid: String?,
    val authProvider: String?,
    val storeId: Int?,
    val createdAt: Long,
)
