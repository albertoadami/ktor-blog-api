package it.adami.services.blog.routes.json

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(val title: String, val text: String)