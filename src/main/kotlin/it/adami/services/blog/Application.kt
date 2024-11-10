package it.adami.services.blog

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import it.adami.services.blog.config.loadConfiguration
import it.adami.services.blog.db.migrateDatabase
import it.adami.services.blog.plugins.*
import kotlin.system.exitProcess

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = loadConfiguration(environment)
    migrateDatabase(config.databaseConfig)


    configureRouting()
}