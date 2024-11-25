package it.adami.services.blog.routes

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import it.adami.services.blog.authentication.Authentication
import it.adami.services.blog.hashing.BCryptHashingData
import it.adami.services.blog.helpers.createNewUser
import it.adami.services.blog.routes.json.GeneratedTokenResponse
import it.adami.services.blog.routes.json.LoginRequest
import it.adami.services.blog.service.UserService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class AuthenticationRoutesTest : WordSpec({

    class Fixtures {
        val userService: UserService = mock()
        val authentication: Authentication = mock()
        val authenticationRoutes: AuthenticationRoutes = AuthenticationRoutes(userService, authentication)
    }

    fun ApplicationTestBuilder.configureApplication(f: Fixtures) {
        application {
            this@application.install(ContentNegotiation) {
                json()
            }
            routing {
                f.authenticationRoutes.register(this)
            }
        }
    }

    "POST /token" should {

        val email = "test@test.it"
        val password = "password"
        val user = createNewUser().copy(email = email, password = BCryptHashingData.encrypt(password))

        "return 200 with token when the login was successfully" {
            testApplication {

                val f = Fixtures()
                configureApplication(f)

                whenever(f.userService.login(email, password)).thenReturn(user)
                whenever(f.authentication.generateToken(user.email, user.id, user.status)).thenReturn("token")

                val loginRequest = LoginRequest(email, password)


                val response = client.post("/token") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(loginRequest))
                }

                response.status shouldBe HttpStatusCode.OK
                val responseBodyString = response.bodyAsText()
                val responseBody = Json.decodeFromString<GeneratedTokenResponse>(responseBodyString)

                responseBody shouldBe GeneratedTokenResponse("token")

            }

        }

        "return 401 if the login was not successfully" {
            testApplication {

                val f = Fixtures()
                configureApplication(f)

                whenever(f.userService.login(email, password)).thenReturn(null)

                val loginRequest = LoginRequest(email, password)


                val response = client.post("/token") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(loginRequest))
                }

                response.status shouldBe HttpStatusCode.Unauthorized

            }

        }

        "return 500 if some unexpected error occurs during login" {
            testApplication {

                val f = Fixtures()
                configureApplication(f)

                whenever(f.userService.login(email, password)).thenThrow(RuntimeException("something unexpected occurred"))

                val loginRequest = LoginRequest(email, password)


                val response = client.post("/token") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(loginRequest))
                }

                response.status shouldBe HttpStatusCode.InternalServerError


            }
        }

    }

})
