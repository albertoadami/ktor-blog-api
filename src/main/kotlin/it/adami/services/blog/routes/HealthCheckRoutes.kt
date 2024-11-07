package it.adami.services.blog.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthCheckRoutes() {
    get("/health") {
        call.respond(HttpStatusCode.NoContent)
    }
}