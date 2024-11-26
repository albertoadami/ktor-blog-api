package it.adami.services.blog.config

import io.ktor.server.application.*

class AppConfig(val databaseConfig: DatabaseConfig, val jwtConfig: JWTConfig)

class DatabaseConfig(val host: String, val port: Int, val name: String, val username: String, val password: String)

class JWTConfig(val secretKey: String, val duration: Long)

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

    val jwtConfig = JWTConfig(secretKey, duration)

    return AppConfig(databaseConfig, jwtConfig)
}