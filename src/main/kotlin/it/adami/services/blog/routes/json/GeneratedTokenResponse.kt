package it.adami.services.blog.routes.json

import kotlinx.serialization.Serializable

@Serializable
data class GeneratedTokenResponse(val token: String)