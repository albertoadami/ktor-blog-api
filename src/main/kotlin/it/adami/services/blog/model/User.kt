package it.adami.services.blog.model

import java.time.Instant

data class User(val id: Long = -1,
                val name: String,
                val surname: String,
                val email: String,
                val password: String,
                val status: UserStatus,
                val createdAt: Instant,
                val updatedAt: Instant)