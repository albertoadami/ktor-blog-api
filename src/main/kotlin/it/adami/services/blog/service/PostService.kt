package it.adami.services.blog.service

import it.adami.services.blog.model.Post
import it.adami.services.blog.repository.PostRepository

interface PostService {

    suspend fun create(post: Post): Long
}

class PostServiceRules(private val postRepository: PostRepository) : PostService {

    override suspend fun create(post: Post): Long =
        postRepository.create(post)


}