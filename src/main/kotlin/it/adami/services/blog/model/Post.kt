package it.adami.services.blog.model

import java.time.Instant

data class Post(val id: Long = -1,
                val title: String,
                val text: String,
                val authorId: Long,
                val upVotes: Int,
                val downVotes: Int,
                val createdAt: Instant,
                val updatedAt: Instant)