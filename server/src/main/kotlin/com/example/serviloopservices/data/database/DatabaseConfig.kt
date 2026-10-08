package com.example.serviloopservices.data.database

import java.net.URI

/**
 * Configuración de conexión a PostgreSQL.
 *
 * Acepta una URL JDBC (`jdbc:postgresql://...`) o una URL de proveedor
 * (`postgresql://user:pass@host:port/db`, como la de Render `DATABASE_URL`).
 */
data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String?,
    val password: String?,
    val maxPoolSize: Int = 5,
) {
    companion object {
        fun from(env: Map<String, String>): DatabaseConfig {
            val raw = env["DATABASE_URL"]
                ?: env["JDBC_DATABASE_URL"]
                ?: "postgresql://postgres:postgres@localhost:5432/serviloop"
            val (jdbcUrl, userFromUrl, passwordFromUrl) = toJdbc(raw)
            return DatabaseConfig(
                jdbcUrl = jdbcUrl,
                username = env["DB_USER"] ?: env["DATABASE_USER"] ?: userFromUrl,
                password = env["DB_PASSWORD"] ?: env["DATABASE_PASSWORD"] ?: passwordFromUrl,
                maxPoolSize = env["DB_POOL_SIZE"]?.toIntOrNull() ?: 5,
            )
        }

        private fun toJdbc(raw: String): Triple<String, String?, String?> {
            if (raw.startsWith("jdbc:")) return Triple(raw, null, null)
            val uri = URI(raw)
            val host = uri.host ?: "localhost"
            val port = if (uri.port == -1) 5432 else uri.port
            val database = uri.path?.removePrefix("/")?.takeIf { it.isNotBlank() } ?: "serviloop"
            val credentials = uri.userInfo?.split(":", limit = 2)
            return Triple(
                "jdbc:postgresql://$host:$port/$database",
                credentials?.getOrNull(0),
                credentials?.getOrNull(1),
            )
        }
    }
}
