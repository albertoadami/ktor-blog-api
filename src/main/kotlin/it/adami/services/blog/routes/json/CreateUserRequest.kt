package it.adami.services.blog.routes.json

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(val name: String, val surname: String, val email: String, val password: String)