package it.adami.services.blog.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import it.adami.services.blog.converter.*
import it.adami.services.blog.routes.json.CreateUserRequest
import it.adami.services.blog.service.UserService

class UserRoutes(private val userService: UserService) {

    fun Route.routes() {
        route("users") {
            post {
                val userRequest = call.receive<CreateUserRequest>()
                try {
                    val createdIdOpt = userService.create(fromCreateUserRequestToUser(userRequest))

                    when (createdIdOpt) {
                        null -> call.respond(HttpStatusCode.Conflict)
                        else -> call.respond(HttpStatusCode.Created)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }


            }
        }
    }

    fun register(route: Route) {
        route.apply { routes() }
    }
}