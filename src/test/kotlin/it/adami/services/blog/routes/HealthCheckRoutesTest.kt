package it.adami.services.blog.routes


import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.server.routing.*

class HealthCheckRoutesTest: WordSpec({

    "/health" should {
        "return 204 when the application is working" {
            testApplication {
                application {
                    routing {
                        healthCheckRoutes()
                    }
                }
                client.get("/health").apply {
                    status shouldBe HttpStatusCode.NoContent
                }
            }


        }
    }
})