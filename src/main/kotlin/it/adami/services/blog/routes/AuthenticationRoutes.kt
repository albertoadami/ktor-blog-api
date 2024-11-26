package it.adami.services.blog.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import it.adami.services.blog.authentication.Authentication
import it.adami.services.blog.routes.json.GeneratedTokenResponse
import it.adami.services.blog.routes.json.LoginRequest
import it.adami.services.blog.service.UserService

class AuthenticationRoutes(private val userService: UserService, private val authentication: Authentication) {

    private fun Route.routes() {
        route("token") {
            post {
                val loginRequest = call.receive<LoginRequest>()
                try {
                    when(val user = userService.login(loginRequest.email, loginRequest.password)) {
                        null -> call.respond(HttpStatusCode.Unauthorized)
                        else -> {
                            val token = authentication.generateToken(user.email, user.id, user.status)
                            val response = GeneratedTokenResponse(token)
                            call.respond(response)
                        }
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