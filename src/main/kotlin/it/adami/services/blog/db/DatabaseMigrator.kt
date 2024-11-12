package it.adami.services.blog.db

import io.ktor.server.application.*
import it.adami.services.blog.config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun migrateDatabase(dbConfig: DatabaseConfig) {
    val flyway = Flyway.configure()
        .dataSource(
            "jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}",
            dbConfig.username,
            dbConfig.password
        )
        .load()
    flyway.migrate()
}

fun configureDatabase(dbConfig: DatabaseConfig) {
    Database.connect(
        "jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}",
        driver = "org.postgresql.Driver",
        user = dbConfig.username,
        password = dbConfig.password
    )
}

