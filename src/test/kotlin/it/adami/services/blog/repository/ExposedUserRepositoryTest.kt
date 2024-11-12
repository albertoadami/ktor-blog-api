package it.adami.services.blog.repository

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
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

    "ExposedUserRepository" should {
        "create successfully a user and return a new id" {
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
            result shouldNotBe  null
        }

        "return null if the email is already in use" {
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

})
