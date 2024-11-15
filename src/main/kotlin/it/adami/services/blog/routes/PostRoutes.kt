package it.adami.services.blog.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import it.adami.services.blog.converter.toDomain
import it.adami.services.blog.routes.json.CreatePostRequest
import it.adami.services.blog.service.PostService
import org.jetbrains.exposed.sql.exposedLogger

class PostRoutes(private val postService: PostService) {

    private fun Route.routes() {

        route("users/{userId}/posts") {
            post {
                val userId = call.parameters["userId"]!!.toLong()
                val createPostRequest = call.receive<CreatePostRequest>()

                try {
                    val postId = postService.create(createPostRequest.toDomain(userId))
                    val locationUri = "/users/$userId/posts/$postId"
                    call.response.headers.append(HttpHeaders.Location, locationUri)
                    call.respond(HttpStatusCode.Created)
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