package it.adami.services.blog.service

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import it.adami.services.blog.helpers.createNewPost
import it.adami.services.blog.repository.PostRepository
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class PostServiceRulesTest : WordSpec({

    class Fixtures {
        val postRepository: PostRepository = mock()
        val postService: PostService = PostServiceRules(postRepository)
    }

    "PostServiceRules.create()" should {

        "return the id of the created post" {
            val f = Fixtures()

            val post = createNewPost("test", 1L)

            whenever(f.postRepository.create(post)).thenReturn(1L)

            val result = f.postService.create(post)

            result shouldBe 1L
        }
    }


})