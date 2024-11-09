package it.adami.services.blog.converter

import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.routes.json.CreateUserRequest
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