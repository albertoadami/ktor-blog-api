package it.adami.services.blog.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.contentnegotiation.*
import it.adami.services.blog.routes.*
import io.ktor.serialization.kotlinx.json.*
import it.adami.services.blog.config.AppConfig
import it.adami.services.blog.config.DatabaseConfig
import it.adami.services.blog.repository.InMemoryUserRepository
import it.adami.services.blog.service.UserServiceRules
import org.flywaydb.core.Flyway


fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }
    install(ContentNegotiation) {
        json()
    }

    // repository classes
    val userRepository = InMemoryUserRepository()

    //service classes
    val userService = UserServiceRules(userRepository)

    val userRoutes = UserRoutes(userService)

    routing {
       healthCheckRoutes()
        userRoutes.register(this)
    }
}