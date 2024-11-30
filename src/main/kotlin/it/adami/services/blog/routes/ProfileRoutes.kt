package it.adami.services.blog.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import it.adami.services.blog.authentication.Authentication
import it.adami.services.blog.converter.toJson
import it.adami.services.blog.exceptions.UserInInvalidStateException
import it.adami.services.blog.service.UserService

class ProfileRoutes(private val userService: UserService, private val authentication: Authentication) {

    private fun Route.routes() {
        route("profile") {
            authenticate("auth-jwt") {
                get {
                    try {
                        val principal = call.principal<JWTPrincipal>()
                            ?: return@get call.respond(HttpStatusCode.Unauthorized)

                        val token = authentication.extractTokenInfo(principal)
                            ?: return@get call.respond(HttpStatusCode.Unauthorized)

                        val user = userService.getById(token.userId)
                            ?: return@get call.respond(HttpStatusCode.NotFound)

                        call.respond(user.toJson())
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
                route("activate") {
                    post {
                        try {
                            val principal = call.principal<JWTPrincipal>()
                                ?: return@post call.respond(HttpStatusCode.Unauthorized)

                            val token = authentication.extractTokenInfo(principal)
                                ?: return@post call.respond(HttpStatusCode.Unauthorized)

                            userService.activateUser(token.userId)
                            call.respond(HttpStatusCode.NoContent)

                        } catch (e: UserInInvalidStateException) {
                            call.respond(HttpStatusCode.MethodNotAllowed)
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }
                }
            }
        }
    }

    fun register(route: Route) {
        route.apply { routes() }
    }

}