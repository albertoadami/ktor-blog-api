import io.kotest.core.spec.style.WordSpec
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.server.plugins.contentnegotiation.*
import it.adami.services.blog.routes.UserRoutes
import it.adami.services.blog.routes.json.CreateUserRequest
import it.adami.services.blog.service.UserService
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import it.adami.services.blog.exceptions.EmailAlreadyInUseException

import kotlinx.serialization.encodeToString
import org.mockito.kotlin.*
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

    "/users" should {
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

})
