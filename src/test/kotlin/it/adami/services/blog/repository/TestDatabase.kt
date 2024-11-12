package it.adami.services.blog.repository

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

object TestDatabase {
    private val container = PostgreSQLContainer(DockerImageName.parse("postgres:13.4"))

    fun setup(): DataSource {
        if (!container.isRunning) {
            container.start()
        }

        val dataSource = setupDataSource(container)
        val flyway = setupFlyway(dataSource)
        flyway.clean()
        flyway.migrate()
        setupConnection(container)
        return dataSource
    }

    private fun setupDataSource(postgreSQLContainer: PostgreSQLContainer<*>): DataSource {
        return PGSimpleDataSource().apply {
            setURL(postgreSQLContainer.jdbcUrl)
            user = postgreSQLContainer.username
            password = postgreSQLContainer.password
        }
    }

    private fun setupFlyway(dataSource: DataSource) = Flyway(
        Flyway.configure()
            .dataSource(dataSource)
    )

    private fun setupConnection(postgreSQLContainer: PostgreSQLContainer<*>) {
        Database.connect(
            postgreSQLContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgreSQLContainer.username,
            password = postgreSQLContainer.password
        )
    }
}
