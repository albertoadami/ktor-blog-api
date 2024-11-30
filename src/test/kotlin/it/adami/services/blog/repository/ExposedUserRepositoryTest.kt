package it.adami.services.blog.repository

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.adami.services.blog.helpers.*
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
            exec("TRUNCATE TABLE users CASCADE;")
        }
    }

    "ExposedUserRepository.create(user)" should {
        "create successfully a user and return a new id" {
            runBlocking {
                val user = createNewUser()

                val result = repository.create(user)
                result shouldNotBe null
            }
        }

        "return null if the email is already in use" {
            runBlocking {
                val user = createNewUser()

                val result1 = repository.create(user)
                result1 shouldNotBe null

                val result2 = repository.create(user)
                result2 shouldBe null
            }
        }
    }

    "ExposedUserRepository.update(user)" should {

        "return true and update an user when the id exist" {
            runBlocking {
                val now = Instant.parse("2023-01-01T00:00:00Z")
                val user = createNewUser(now)

                val userId = repository.create(user)!!

                val updatedDate = Instant.parse("2023-01-02T00:00:00Z")

                val userToUpdate = user.copy(id = userId, status = UserStatus.ENABLED, updatedAt = updatedDate)

                repository.update(userToUpdate) shouldBe true

                val retrievedUser = repository.getById(userToUpdate.id)!!

                retrievedUser shouldBe userToUpdate
            }
        }

        "return false when no update has been performed" {
            runBlocking {
                val user = createNewUser().copy(id = 1L)

                repository.update(user) shouldBe false
            }
        }


    }

    "ExposedUserRepository.getById(id)" should {
        "return the user if it exists" {
                val now = Instant.parse("2023-01-01T00:00:00Z")
                val user = createNewUser(now)

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

    "ExposedUserRepository.getByEmail(email)" should {

        "return the user if it exists" {
            runBlocking {
                val now = Instant.parse("2023-01-01T00:00:00Z")
                val user = createNewUser(now)

                repository.create(user)

                val retrievedUser = repository.getByEmail(user.email)!!

                retrievedUser.name shouldBe user.name
                retrievedUser.surname shouldBe user.surname
                retrievedUser.email shouldBe user.email
                retrievedUser.password shouldBe user.password
                retrievedUser.status shouldBe user.status
                retrievedUser.createdAt shouldBe user.createdAt
                retrievedUser.updatedAt shouldBe user.updatedAt

            }

        }

        "return null if the email doesn't exist" {
            runBlocking {
                repository.getByEmail("test@test.it") shouldBe null
            }
        }

    }

    "ExposedUserRepository.deleteById(id)" should {

        "return true if the user exist" {
            runBlocking {
                val now = Instant.parse("2023-01-01T00:00:00Z")
                val user = createNewUser(now)

                val id = repository.create(user)!!

                val result = repository.deleteById(id)

                result shouldBe true

            }
        }

        "return false if the user doesn't exist" {
            runBlocking {
                val result = repository.deleteById(999L)

                result shouldBe false
            }
        }
    }
})
