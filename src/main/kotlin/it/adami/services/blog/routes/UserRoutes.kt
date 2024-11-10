package it.adami.services.blog.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import it.adami.services.blog.converter.*
import it.adami.services.blog.exceptions.EmailAlreadyInUse
import it.adami.services.blog.routes.json.CreateUserRequest
import it.adami.services.blog.service.UserService
import org.jetbrains.exposed.sql.exposedLogger

class UserRoutes(private val userService: UserService) {

    fun Route.routes() {
        route("users") {
            post {
                val userRequest = call.receive<CreateUserRequest>()
                try {
                    val createdId = userService.create(toDomain(userRequest))
                    val locationUri = "/users/$createdId"
                    call.response.headers.append(HttpHeaders.Location, locationUri)
                    call.respond(HttpStatusCode.Created)
                } catch (e: EmailAlreadyInUse) {
                    call.respond(HttpStatusCode.Conflict)
                } catch (e: Exception) {
                    exposedLogger.error(e)
                    call.respond(HttpStatusCode.InternalServerError)
                }


            }
        }
    }

    fun register(route: Route) {
        route.apply { routes() }
    }
}