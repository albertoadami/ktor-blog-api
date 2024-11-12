package it.adami.services.blog.routes.json

import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.util.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.reflect.jvm.internal.impl.types.checker.TypeRefinementSupport.Enabled


@Serializable
data class GetUserResponse(val id: Long,
                           val name: String,
                           val surname: String,
                           val email: String,
                           val enabled: Boolean,
                           @Serializable(with = InstantSerializer::class)
                           val createdAt: Instant,
                           @Serializable(with = InstantSerializer::class)
                           val updatedAt: Instant)