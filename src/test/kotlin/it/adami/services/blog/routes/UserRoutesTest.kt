package it.adami.services.blog.routes

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.server.plugins.contentnegotiation.*
import it.adami.services.blog.routes.json.CreateUserRequest
import it.adami.services.blog.service.UserService
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import it.adami.services.blog.converter.toJson
import it.adami.services.blog.exceptions.EmailAlreadyInUseException
import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.routes.json.GetUserResponse

import kotlinx.serialization.encodeToString
import org.mockito.kotlin.*
import java.time.Instant
import kotlin.test.assertEquals

class UserRoutesTest: WordSpec({

    class Fixtures {
        val userService: UserService = mock<UserService>()
        val userRoutes: UserRoutes = UserRoutes(userService)
    }

    fun ApplicationTestBuilder.configureApplication(f: Fixtures) {
        application {
            this@application.install(ContentNegotiation) {
                json()
            }
            routing {
                f.userRoutes.register(this)
            }
        }
    }

    "POST /users" should {
        "return 201 when the user is created successfully" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.userService.create(any())).thenReturn(1L)

                val createUserJson = CreateUserRequest("test", "test", "test@test.it", "password")

                val response = client.post("/users") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(createUserJson))
                }

                assertEquals(HttpStatusCode.Created, response.status)
            }
        }


        "return 409 when the username is already in use" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.userService.create(any())).thenThrow(EmailAlreadyInUseException("test@test.it"))

                val createUserJson = CreateUserRequest("test", "test", "test@test.it", "password")

                val response = client.post("/users") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(createUserJson))
                }

                assertEquals(HttpStatusCode.Conflict, response.status)
            }
        }

        "return 500 when an unexpected error occurred" {
            testApplication {
                val f = Fixtures()

                configureApplication(f)

                whenever(f.userService.create(any())).thenThrow(RuntimeException("some error"))

                val createUserJson = CreateUserRequest("test", "test", "test@test.it", "password")

                val response = client.post("/users") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(createUserJson))
                }

                assertEquals(HttpStatusCode.InternalServerError, response.status)
            }
        }
    }

    "GET /users/{userId}" should {

        val user = User(
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            password = "password",
            status = UserStatus.PENDING,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        "return 200 status when the user exists" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)


                whenever(f.userService.getById(1)).thenReturn(user)

                val response = client.get("/users/1") {
                    contentType(ContentType.Application.Json)
                }

                response.status shouldBe HttpStatusCode.OK
                val responseBodyString = response.bodyAsText()
                val responseBody = Json.decodeFromString<GetUserResponse>(responseBodyString)

                responseBody shouldBe toJson(user)


            }
        }


        "return 404 if the user doesn't exist" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)


                whenever(f.userService.getById(1)).thenReturn(null)

                val response = client.get("/users/1") {
                    contentType(ContentType.Application.Json)
                }

                response.status shouldBe HttpStatusCode.NotFound

            }

        }

        "return 500 if an unexpected error occurred" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)


                whenever(f.userService.getById(1)).thenThrow(RuntimeException("unexpected error"))

                val response = client.get("/users/1") {
                    contentType(ContentType.Application.Json)
                }

                response.status shouldBe HttpStatusCode.InternalServerError

            }

        }
    }


})
