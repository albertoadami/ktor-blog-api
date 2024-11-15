package it.adami.services.blog

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import it.adami.services.blog.config.loadConfiguration
import it.adami.services.blog.db.migrateDatabase
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.contentnegotiation.*
import it.adami.services.blog.routes.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import it.adami.services.blog.db.configureDatabase
import it.adami.services.blog.repository.ExposedPostRepository
import it.adami.services.blog.repository.ExposedUserRepository
import it.adami.services.blog.service.PostServiceRules
import it.adami.services.blog.service.UserServiceRules
import org.slf4j.event.Level
import kotlin.system.measureTimeMillis

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = loadConfiguration(environment)
    installComponents()
    migrateDatabase(config.databaseConfig)
    configureDatabase(config.databaseConfig)

    // repository classes
    val userRepository = ExposedUserRepository()
    val postRepository = ExposedPostRepository()

    //service classes
    val userService = UserServiceRules(userRepository)
    val postService = PostServiceRules(postRepository)

    val userRoutes = UserRoutes(userService)
    val postRoutes = PostRoutes(postService)

    routing {
        healthCheckRoutes()
        userRoutes.register(this)
        postRoutes.register(this)
    }
}

fun Application.installComponents() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging) {
        level = Level.INFO  // Set the log level (e.g., DEBUG, INFO, WARN)
        format { call ->
            // Measure time spent to process the request
            val timeSpent = measureTimeMillis {
                // Log the status and time spent
                call.response.status() // Ensure the response has been set
            }
            "Request: ${call.request.call.request.httpMethod} ${call.request.call.request.uri} | Status: ${call.response.status()} | Time: $timeSpent ms"
        }
    }
}