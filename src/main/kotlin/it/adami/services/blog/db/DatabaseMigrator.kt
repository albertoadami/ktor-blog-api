package it.adami.services.blog.db

import io.ktor.server.application.*
import it.adami.services.blog.config.DatabaseConfig
import org.flywaydb.core.Flyway

fun Application.migrateDatabase(dbConfig: DatabaseConfig) {
    println("Database Config: host=${dbConfig.host}, port=${dbConfig.port}, name=${dbConfig.name}")
    val jdbcUrl = "jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}"
    print("ursl: $jdbcUrl")
    val flyway = Flyway.configure()
        .dataSource(
            "jdbc:postgresql://localhost:5432/blog",  // Use localhost for Flyway connection
            "postgres",  // Username
            "password"   // Password
        )
        .load()
    flyway.migrate()
}

