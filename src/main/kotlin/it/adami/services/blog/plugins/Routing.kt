package it.adami.services.blog.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.contentnegotiation.*
import it.adami.services.blog.routes.*
import io.ktor.serialization.kotlinx.json.*
import it.adami.services.blog.service.InMemoryUserService


fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }
    install(ContentNegotiation) {
        json()
    }

    val userRoutes = UserRoutes(InMemoryUserService())

    routing {
       healthCheckRoutes()
        userRoutes.register(this)
    }
}