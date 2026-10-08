This is a Kotlin Multiplatform project targeting Server.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

### Arquitectura

El servidor sigue capas `routes → domain/usecase → domain/repository → data/repositoryImpl → data/database` (ver [`ANGENTS.md`](ANGENTS.md)).

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and
options:

- PostgreSQL local: `docker compose up -d`
- Server: `./gradlew :server:run`

### Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `PORT` | `8080` | Puerto del servidor |
| `DATABASE_URL` | **requerido** | JDBC (`jdbc:postgresql://…`) o proveedor (`postgres://` / `postgresql://user:pass@host:port/db`). En hosts remotos agrega `sslmode=require` |
| `DB_USER` / `DB_PASSWORD` | credenciales del URL | Usuario y contraseña si no vienen en el URL |
| `DB_POOL_SIZE` | `10` | Tamaño del pool Hikari |
| `JWT_SECRET` | `dev-insecure-secret-change-me` | Secreto HMAC256 (**cambiar en producción**) |
| `JWT_ISSUER` | `serviloopservices` | Emisor del JWT |
| `JWT_AUDIENCE` | `serviloopservices-client` | Audiencia del JWT |
| `JWT_REALM` | `serviloopservices` | Realm del challenge `401` |
| `JWT_EXPIRY_SECONDS` | `3600` | Vigencia del token en segundos |

### API de autenticación

Base: `/auth`. Los errores se devuelven como `{ "error": "<AuthErrorCode>" }`.

| Método | Ruta | Auth | Cuerpo | Respuesta |
|--------|------|------|--------|-----------|
| `POST` | `/auth/register` | — | `{ email, password, name, deviceToken? }` | `200` `AuthResponse` |
| `POST` | `/auth/login` | — | `{ email, password, deviceToken? }` | `200` `AuthResponse` |
| `POST` | `/auth/federated` | — | `{ idToken?, provider?, name?, deviceToken? }` | `200` `AuthResponse` |
| `GET` | `/auth/me` | Bearer | — | `200` `UserResponse` |
| `POST` | `/auth/refresh` | Bearer | — | `200` `AuthResponse` |
| `PUT` | `/auth/me/device-token` | Bearer | `{ deviceToken? }` | `204` |

`AuthResponse`: `{ token, tokenType: "Bearer", expiresIn, user: { id, email, name, storeId? } }`.

Códigos de error (`data/enum/AuthErrorCode.kt`): `INVALID_EMAIL`, `INVALID_PASSWORD`, `INVALID_NAME`,
`INVALID_TOKEN`, `INVALID_PROVIDER`, `INVALID_CREDENTIALS`, `EMAIL_ALREADY_REGISTERED`, `USER_NOT_FOUND`,
`UNAUTHORIZED`.

El login federado usa `FederatedIdentityVerifier`; por defecto está deshabilitado
(`DisabledFederatedIdentityVerifier`) y se debe inyectar una implementación real (Firebase Admin) en
`Application.module()`.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
