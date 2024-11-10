package it.adami.services.blog.db

import io.ktor.server.application.*
import it.adami.services.blog.config.DatabaseConfig
import org.flywaydb.core.Flyway

fun Application.migrateDatabase(dbConfig: DatabaseConfig) {
    val flyway = Flyway.configure()
        .dataSource(
            "jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}",  // Use localhost for Flyway connection
            dbConfig.username,  // Username
            dbConfig.password   // Password
        )
        .load()
    flyway.migrate()
}

