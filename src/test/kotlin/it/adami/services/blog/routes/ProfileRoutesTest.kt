package it.adami.services.blog.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import it.adami.services.blog.authentication.Authentication
import it.adami.services.blog.authentication.TokenInfo
import it.adami.services.blog.converter.toJson
import it.adami.services.blog.exceptions.UserInInvalidStateException
import it.adami.services.blog.helpers.createNewUser
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.routes.json.GetUserResponse
import it.adami.services.blog.service.UserService
import kotlinx.serialization.json.Json
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ProfileRoutesTest : WordSpec({

    class Fixtures {
        val userService: UserService = mock<UserService>()
        val authentication: Authentication = mock()
        val profileRoutes: ProfileRoutes = ProfileRoutes(userService, authentication)
    }

    fun ApplicationTestBuilder.configureApplication(f: Fixtures) {
        application {
            this@application.install(ContentNegotiation) {
                json()
            }
            this@application.install(io.ktor.server.auth.Authentication) {
                jwt("auth-jwt") {
                    realm = "Access to 'blog-api'"

                    verifier(
                        JWT
                            .require(Algorithm.HMAC256("test-secret")) // Use a mock secret
                            .withIssuer("test-issuer") // Use a mock issuer
                            .build()
                    )

                    // Mock validation to always succeed
                    validate { credential ->
                        JWTPrincipal(credential.payload) // Always return a valid principal
                    }
                }
            }
            routing {
                f.profileRoutes.register(this)
            }
        }
    }

    "GET /profile" should {

        val token = JWT.create()
            .withIssuer("test-issuer")
            .withAudience("test-audience")
            .sign(Algorithm.HMAC256("test-secret"))

        "return 200 with profile json if the token is valid" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                val user = createNewUser().copy(id = 1, email = "test@test.it", status = UserStatus.PENDING)

                whenever(f.authentication.extractTokenInfo(any())).thenReturn(TokenInfo(user.id, user.email, user.status))
                whenever(f.userService.getById(1)).thenReturn(user)

                val response = client.get("/profile") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                response.status shouldBe HttpStatusCode.OK
                val responseBodyString = response.bodyAsText()
                val responseBody = Json.decodeFromString<GetUserResponse>(responseBodyString)
                responseBody shouldBe user.toJson()

            }

        }

        "return 401 if the token is invalid" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                val wrongToken = "wrong"

                val response = client.get("/profile") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $wrongToken")
                    }
                }

                response.status shouldBe HttpStatusCode.Unauthorized
            }
        }

        "return 401 if the token doesn't contains the TokenInfo information" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.authentication.extractTokenInfo(any())).thenReturn(null)

                val response = client.get("/profile") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                response.status shouldBe HttpStatusCode.Unauthorized

            }
        }

        "return 404 if the user doesn't exist" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                val user = createNewUser().copy(id = 1, email = "test@test.it", status = UserStatus.PENDING)

                whenever(f.authentication.extractTokenInfo(any())).thenReturn(TokenInfo(user.id, user.email, user.status))
                whenever(f.userService.getById(1)).thenReturn(null)

                val response = client.get("/profile") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                response.status shouldBe HttpStatusCode.NotFound

            }
        }

        "return 500 if some unexpected exception occurred" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                val user = createNewUser().copy(id = 1, email = "test@test.it", status = UserStatus.PENDING)

                whenever(f.authentication.extractTokenInfo(any())).thenReturn(TokenInfo(user.id, user.email, user.status))
                whenever(f.userService.getById(1)).thenThrow(RuntimeException("unexpected error"))

                val response = client.get("/profile") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                response.status shouldBe HttpStatusCode.InternalServerError

            }

        }


    }

    "POST /profile/activate" should {

        val token = JWT.create()
            .withIssuer("test-issuer")
            .withAudience("test-audience")
            .sign(Algorithm.HMAC256("test-secret"))

        val user = createNewUser().copy(id = 1, email = "test@test.it", status = UserStatus.PENDING)


        "return 204 when the activate step complete successfully" {

            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.authentication.extractTokenInfo(any())).thenReturn(TokenInfo(user.id, user.email, user.status))
                whenever(f.userService.activateUser(user.id)).thenReturn(Unit)

                val response = client.post("/profile/activate") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                response.status shouldBe HttpStatusCode.NoContent


            }

        }

        "return 401 if the token is invalid" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                val response = client.post("/profile/activate") {
                    headers {
                        append(HttpHeaders.Authorization, "wrong-token")
                    }
                }

                response.status shouldBe HttpStatusCode.Unauthorized
            }
        }

        "return 405 if the user is already activated" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.authentication.extractTokenInfo(any())).thenReturn(TokenInfo(user.id, user.email, UserStatus.ENABLED))
                whenever(f.userService.activateUser(user.id)).thenThrow(UserInInvalidStateException(user.id, UserStatus.ENABLED))

                val response = client.post("/profile/activate") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                response.status shouldBe HttpStatusCode.MethodNotAllowed

            }
        }

        "return 500 if some unexpected exception occured" {
            testApplication {
                val f = Fixtures()
                configureApplication(f)

                whenever(f.authentication.extractTokenInfo(any())).thenReturn(TokenInfo(user.id, user.email, user.status))
                whenever(f.userService.activateUser(user.id)).thenThrow(RuntimeException("some error occured"))

                val response = client.post("/profile/activate") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                response.status shouldBe HttpStatusCode.InternalServerError

            }
        }

    }


})