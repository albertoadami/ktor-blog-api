package it.adami.services.blog.routes.json

import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.util.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class GetUserResponse(val id: Long,
                           val name: String,
                           val surname: String,
                           val email: String,
                           val status: UserStatus,
                           @Serializable(with = InstantSerializer::class)
                           val createdAt: Instant,
                           @Serializable(with = InstantSerializer::class)
                           val updatedAt: Instant)