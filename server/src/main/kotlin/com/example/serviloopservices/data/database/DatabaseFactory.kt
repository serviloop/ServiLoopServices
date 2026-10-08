package com.example.serviloopservices.data.database

import com.example.serviloopservices.data.database.user.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    fun init(config: DatabaseConfig) {
        val hikari = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            config.username?.let { username = it }
            config.password?.let { password = it }
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = config.maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        Database.connect(HikariDataSource(hikari))
        transaction {
            SchemaUtils.create(Users)
        }
    }
}
