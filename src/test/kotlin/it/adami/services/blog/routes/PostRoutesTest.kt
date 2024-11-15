package it.adami.services.blog.routes

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import it.adami.services.blog.routes.json.CreatePostRequest
import it.adami.services.blog.service.PostService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mockito.kotlin.*

class PostRoutesTest: WordSpec({

    class Fixtures {
        val postService: PostService = mock()
        val postRoutes: PostRoutes = PostRoutes(postService)
    }

    fun ApplicationTestBuilder.configureApplication(f: Fixtures) {
        application {
            this@application.install(ContentNegotiation) {
                json()
            }
            routing {
                f.postRoutes.register(this)
            }
        }
    }

    "POST /users/{userId}/posts" should {

        "return 201 with the id of new post if the creation succeed" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.postService.create(any())).thenReturn(123L)

                val postRequest = CreatePostRequest("test", "my first post")

                val response = client.post("/users/1/posts") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(postRequest))
                }

                response.status shouldBe HttpStatusCode.Created
                response.headers["Location"]!! shouldBe "/users/1/posts/123"
            }

        }

        "return 500 if an unexpected error occurred" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.postService.create(any())).thenThrow(RuntimeException("some error occurred"))

                val postRequest = CreatePostRequest("test", "my first post")

                val response = client.post("/users/1/posts") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(postRequest))
                }

                response.status shouldBe HttpStatusCode.InternalServerError

            }
        }

    }

})
