package it.adami.services.blog.repository

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class ExposedUserRepositoryTest : WordSpec({

    lateinit var repository: UserRepository

    beforeTest {
        TestDatabase.setup()
        repository = ExposedUserRepository()
    }

    afterTest {
        transaction {
            exec("TRUNCATE TABLE users;")
        }
    }

    "ExposedUserRepository.create(user)" should {
        "create successfully a user and return a new id" {
            runBlocking {
                val user = User(
                    id = -1,
                    name = "test",
                    surname = "test",
                    email = "test@test.it",
                    password = "test",
                    status = UserStatus.PENDING,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )

                val result = repository.create(user)
                result shouldNotBe null
            }
        }

        "return null if the email is already in use" {
            runBlocking {
                val user = User(
                    id = -1,
                    name = "test",
                    surname = "test",
                    email = "test@test.it",
                    password = "test",
                    status = UserStatus.PENDING,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )

                val result1 = repository.create(user)
                result1 shouldNotBe null

                val result2 = repository.create(user)
                result2 shouldBe null
            }
        }
    }

    "ExposedUserRepository.getById(id)" should {
        "return the user if it exists" {
                val now = Instant.parse("2023-01-01T00:00:00Z")
                val user = User(
                    id = -1,
                    name = "test",
                    surname = "test",
                    email = "test@test.it",
                    password = "test",
                    status = UserStatus.PENDING,
                    createdAt = now,
                    updatedAt = now
                )

                val userId = runBlocking {  repository.create(user)}!!

                val retrievedUser = runBlocking { repository.getById(userId)}!!

                retrievedUser.name shouldBe user.name
                retrievedUser.surname shouldBe user.surname
                retrievedUser.email shouldBe user.email
                retrievedUser.password shouldBe user.password
                retrievedUser.status shouldBe user.status
                retrievedUser.createdAt shouldBe user.createdAt
                retrievedUser.updatedAt shouldBe user.updatedAt

        }

        "return null if the id doesn't exist" {
            runBlocking {
                val result = repository.getById(999L)
                result shouldBe null
            }
        }
    }
})
