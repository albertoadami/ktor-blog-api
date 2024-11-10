package it.adami.services.blog.config

import io.ktor.server.application.*

class AppConfig(val databaseConfig: DatabaseConfig)

class DatabaseConfig(val host: String, val port: Int, val name: String, val username: String, val password: String)

fun loadConfiguration(environment: ApplicationEnvironment): AppConfig {
    val config = environment.config.config("database")

    val host = config.property("host").getString()
    val port = config.property("port").getString().toInt()
    val name = config.property("name").getString()
    val username = config.property("username").getString()
    val password = config.property("password").getString()

    val databaseConfig = DatabaseConfig(host, port, name, username, password)

    return AppConfig(databaseConfig)
}