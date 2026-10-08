package com.example.serviloopservices.data.database

import com.example.serviloopservices.data.database.user.AUTH_PROVIDER_MAX_LENGTH
import com.example.serviloopservices.data.database.user.DEVICE_TOKEN_MAX_LENGTH
import com.example.serviloopservices.data.database.user.FIREBASE_UID_MAX_LENGTH
import com.example.serviloopservices.data.database.user.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    fun init(config: DatabaseConfig) {
        val dataSource = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = config.url
                username = config.user
                password = config.password
                driverClassName = "org.postgresql.Driver"
                maximumPoolSize = config.maxPoolSize
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            },
        )
        Database.connect(dataSource)
        transaction {
            SchemaUtils.create(Users)
        }
    }

}
