package com.example.serviloopservices.data.database

import java.net.URI

/**
 * Configuración de conexión a PostgreSQL leída de variables de entorno.
 *
 * Acepta una URL JDBC (`jdbc:postgresql://...`) o una URL de proveedor
 * (`postgres://...` / `postgresql://user:pass@host:port/db`, como `DATABASE_URL`
 * de Render). Para hosts remotos agrega `sslmode=require` automáticamente.
 */
data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int,
) {
    companion object {
        private const val DEFAULT_MAX_POOL_SIZE = 10
        private const val DEFAULT_PORT = 5432
        private const val DEFAULT_DATABASE = "serviloop"

        fun from(env: Map<String, String> = System.getenv()): DatabaseConfig {
            val rawUrl = (env["DATABASE_URL"] ?: env["JDBC_DATABASE_URL"])
                ?.takeIf { it.isNotBlank() }
                ?: error("DATABASE_URL is not set")
            val configuredUser = env["DB_USER"] ?: env["DATABASE_USER"].orEmpty()
            val configuredPassword = env["DB_PASSWORD"] ?: env["DATABASE_PASSWORD"].orEmpty()
            val maxPoolSize = env["DB_POOL_SIZE"]?.toIntOrNull() ?: DEFAULT_MAX_POOL_SIZE
            if (rawUrl.startsWith("jdbc:")) {
                return DatabaseConfig(rawUrl, configuredUser, configuredPassword, maxPoolSize)
            }
            val parsed = parseConnectionString(rawUrl, maxPoolSize)
            return DatabaseConfig(
                url = parsed.url,
                user = configuredUser.ifBlank { parsed.user },
                password = configuredPassword.ifBlank { parsed.password },
                maxPoolSize = maxPoolSize,
            )
        }

        private fun parseConnectionString(connectionString: String, maxPoolSize: Int): DatabaseConfig {
            val uri = URI(connectionString.replaceFirst("postgres://", "postgresql://"))
            val credentials = uri.userInfo?.split(":", limit = 2).orEmpty()
            val host = uri.host ?: "localhost"
            val port = if (uri.port > 0) uri.port else DEFAULT_PORT
            val database = uri.path.removePrefix("/").substringBefore("?").ifBlank { DEFAULT_DATABASE }
            val isLocal = host == "localhost" || host == "127.0.0.1" || host == "::1"
            val sslMode = if (!isLocal && host.contains('.')) "?sslmode=require" else ""
            return DatabaseConfig(
                url = "jdbc:postgresql://$host:$port/$database$sslMode",
                user = credentials.getOrNull(0).orEmpty(),
                password = credentials.getOrNull(1).orEmpty(),
                maxPoolSize = maxPoolSize,
            )
        }
    }
}
