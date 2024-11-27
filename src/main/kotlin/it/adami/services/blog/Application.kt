package it.adami.services.blog

import com.auth0.jwt.JWT
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
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import it.adami.services.blog.authentication.Authentication
import it.adami.services.blog.authentication.JWTAuthentication
import it.adami.services.blog.db.configureDatabase
import it.adami.services.blog.repository.ExposedPostRepository
import it.adami.services.blog.repository.ExposedUserRepository
import it.adami.services.blog.service.PostServiceRules
import it.adami.services.blog.service.UserServiceRules
import org.slf4j.event.Level
import kotlin.system.measureTimeMillis
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import it.adami.services.blog.config.JWTConfig


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = loadConfiguration(environment)
    installComponents(config.jwtConfig)
    migrateDatabase(config.databaseConfig)
    configureDatabase(config.databaseConfig)

    // repository classes
    val userRepository = ExposedUserRepository()
    val postRepository = ExposedPostRepository()

    val authentication: Authentication = JWTAuthentication(config.jwtConfig)

    //service classes
    val userService = UserServiceRules(userRepository)
    val postService = PostServiceRules(postRepository)

    val userRoutes = UserRoutes(userService)
    val postRoutes = PostRoutes(postService)
    val authenticationRoutes = AuthenticationRoutes(userService, authentication)
    val profileRoutes = ProfileRoutes(userService, authentication)

    routing {
        healthCheckRoutes()
        userRoutes.register(this)
        postRoutes.register(this)
        authenticationRoutes.register(this)
        profileRoutes.register(this)
    }
}

fun Application.installComponents(config: JWTConfig) {
    installStatusPages()
    installJsonSupport()
    installCallLogging()
    configureAuthentication(config)
}

fun Application.installStatusPages() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }
}

fun Application.installJsonSupport() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.installCallLogging() {
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

fun Application.configureAuthentication(jwtConfig: JWTConfig) {
    install(io.ktor.server.auth.Authentication) {
        jwt("auth-jwt") {
            realm = "Access to 'blog-api'"

            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secretKey))
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.audience.contains(jwtConfig.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid or missing token")
            }
        }
    }
}
