package it.adami.services.blog.config

import io.ktor.server.application.*

class AppConfig(val databaseConfig: DatabaseConfig, val jwtConfig: JWTConfig)

class DatabaseConfig(val host: String, val port: Int, val name: String, val username: String, val password: String)

class JWTConfig(val secretKey: String, val duration: Long, val issuer: String, val audience: String)

fun loadConfiguration(environment: ApplicationEnvironment): AppConfig {
    val databaseConfigs = environment.config.config("database")
    val jwtConfigs = environment.config.config("jwt")

    val host = databaseConfigs.property("host").getString()
    val port = databaseConfigs.property("port").getString().toInt()
    val name = databaseConfigs.property("name").getString()
    val username = databaseConfigs.property("username").getString()
    val password = databaseConfigs.property("password").getString()

    val databaseConfig = DatabaseConfig(host, port, name, username, password)

    val secretKey = jwtConfigs.property("secretKey").getString()
    val duration = jwtConfigs.property("duration").getString().toLong()
    val issuer = jwtConfigs.property("issuer").getString()
    val audience = jwtConfigs.property("audience").getString()

    val jwtConfig = JWTConfig(secretKey, duration, issuer, audience)

    return AppConfig(databaseConfig, jwtConfig)
}