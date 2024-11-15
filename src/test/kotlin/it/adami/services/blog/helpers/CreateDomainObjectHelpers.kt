package it.adami.services.blog.helpers

import it.adami.services.blog.model.Post
import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import java.time.Instant

fun createNewUser(createdAt: Instant = Instant.now()): User =
    User(
        id = -1,
        name = "test",
        surname = "test",
        email = "test@test.it",
        password = "test",
        status = UserStatus.PENDING,
        createdAt = createdAt,
        updatedAt = createdAt
    )

fun createNewPost(title: String, authorId: Long, createdAt: Instant = Instant.now()): Post =
    Post(
        id = -1,
        title = title,
        text = "test",
        authorId = authorId,
        upVotes = 0,
        downVotes = 0,
        createdAt = createdAt,
        updatedAt = createdAt
    )