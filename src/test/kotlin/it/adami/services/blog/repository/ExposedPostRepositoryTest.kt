package it.adami.services.blog.repository

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.adami.services.blog.helpers.createNewPost
import it.adami.services.blog.helpers.createNewUser
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedPostRepositoryTest: WordSpec ({

    lateinit var userRepository: UserRepository
    lateinit var postRepository: PostRepository

    beforeTest {
        TestDatabase.setup()
        userRepository = ExposedUserRepository()
        postRepository = ExposedPostRepository()
    }

    afterTest {
        transaction {
            exec("TRUNCATE TABLE posts CASCADE;")
        }
    }

    "ExposedPostRepositoryTest.create(post)" should {

        "store a new entity successfully" {

            runBlocking {
                val user = createNewUser()
                val userId = userRepository.create(user)!!

                val post = createNewPost("test post", userId)
                val postId = postRepository.create(post)

                val result = postRepository.getById(postId)!!

                result.copy(id = postId) shouldBe result

            }
        }

    }

    "ExposedPostRepositoryTest.getById(id)" should {

        "return the post if the id exist in the system" {

            runBlocking {
                val user = createNewUser()
                val userId = userRepository.create(user)!!

                val post = createNewPost("test", userId)
                val postId = postRepository.create(post)

                postRepository.getById(postId).shouldNotBe(null)
            }
        }

        "return null if the post doesn't exist" {

            runBlocking {
                val result = postRepository.getById(999L)

                result shouldBe null
            }
        }
    }





})