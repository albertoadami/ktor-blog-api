package it.adami.services.blog.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import it.adami.services.blog.converter.*
import it.adami.services.blog.exceptions.EmailAlreadyInUseException
import it.adami.services.blog.exceptions.UserNotFoundException
import it.adami.services.blog.routes.json.CreateUserRequest
import it.adami.services.blog.service.UserService
import org.jetbrains.exposed.sql.exposedLogger

class UserRoutes(private val userService: UserService) {

    private fun Route.routes() {
        route("users") {

            route("register") {
                // POST /users/register
                post {
                    val userRequest = call.receive<CreateUserRequest>()
                    try {
                        val createdId = userService.create(userRequest.toDomain())
                        val locationUri = "/users/$createdId"
                        call.response.headers.append(HttpHeaders.Location, locationUri)
                        call.respond(HttpStatusCode.Created)
                    } catch (e: EmailAlreadyInUseException) {
                        call.respond(HttpStatusCode.Conflict)
                    } catch (e: Exception) {
                        exposedLogger.error(e)
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }

            // GET /users/{userId}
            get("{userId}") {
                val userId = call.parameters["userId"]?.toLongOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")
                    return@get
                }
                try {
                    when (val result = userService.getById(userId)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> call.respond(result.toJson())
                    }
                } catch (e: Exception) {
                    exposedLogger.error(e)
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            // DELETE /users/{userId}
            delete("{userId}") {
                val userId = call.parameters["userId"]?.toLongOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")
                    return@delete
                }
                try {
                    userService.deleteById(userId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: UserNotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (ex: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }

    fun register(route: Route) {
        route.apply { routes() }
    }
}
