package it.adami.services.blog.converter

import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.routes.json.CreateUserRequest
import it.adami.services.blog.routes.json.GetUserResponse
import java.time.Instant

fun toDomain(req: CreateUserRequest): User =
    User(
        id = -1,
        name = req.name,
        surname = req.surname,
        email = req.email,
        password = req.password,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        status = UserStatus.PENDING
    )

fun toJson(user: User): GetUserResponse =
    GetUserResponse(
        id = user.id,
        name = user.name,
        surname = user.surname,
        email = user.email,
        enabled = user.isActive,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt
    )