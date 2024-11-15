package it.adami.services.blog.converter

import it.adami.services.blog.model.Post
import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.routes.json.CreatePostRequest
import it.adami.services.blog.routes.json.CreateUserRequest
import it.adami.services.blog.routes.json.GetUserResponse
import java.time.Instant

fun CreateUserRequest.toDomain(): User =
    User(
        id = -1,
        name = this.name,
        surname = this.surname,
        email = this.email,
        password = this.password,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        status = UserStatus.PENDING
    )

fun CreatePostRequest.toDomain(userId: Long): Post =
    Post(
        id = -1,
        title = this.title,
        text = this.title,
        authorId = userId,
        upVotes = 0,
        downVotes = 0,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

fun User.toJson(): GetUserResponse =
    GetUserResponse(
        id = this.id,
        name = this.name,
        surname = this.surname,
        email = this.email,
        enabled = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )