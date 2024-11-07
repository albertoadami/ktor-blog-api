package it.adami.services.blog.routes


import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.server.routing.*

class HealthCheckRouteTest {

    @Test
    fun testHealthCheckRouteIsReturningNoContent() = testApplication {
        application {
            routing {
                healthCheckRoutes()
            }
        }

        client.get("/health").apply {
            assertEquals(HttpStatusCode.NoContent, status)
        }
    }
}