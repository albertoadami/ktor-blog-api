package it.adami.services.blog.routes.json

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)