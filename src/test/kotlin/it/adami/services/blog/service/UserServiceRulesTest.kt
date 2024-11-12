package it.adami.services.blog.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import it.adami.services.blog.exceptions.EmailAlreadyInUseException
import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.*
import java.time.Instant

class UserServiceRulesTest: WordSpec({

    val userRepository: UserRepository = mock()
    val userService: UserService = UserServiceRules(userRepository)

    "UserServiceRulesTest" should {

        "return the user ID when is created successfully" {
            runBlocking {
                // Arrange
                val user = User(
                    name = "John",
                    surname = "Doe",
                    email = "john.doe@example.com",
                    password = "password",
                    status = UserStatus.PENDING,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                val expectedId = 1L
                whenever(userRepository.create(user)).thenReturn(expectedId)

                // Act
                val result = userService.create(user)

                // Assert
                expectedId shouldBe result
                verify(userRepository).create(user)
            }

        }

        "throw EmailAlaradyInUse when the email is already in use" {
            runBlocking {
                // Arrange
                val user = User(
                    name = "John",
                    surname = "Doe",
                    email = "john.doe@example.com",
                    password = "password",
                    status = UserStatus.PENDING,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                whenever(userRepository.create(user)).thenReturn(null)

                // Act
                val exception = shouldThrow<EmailAlreadyInUseException> {
                    userService.create(user)
                }
                // Assert
                exception.email shouldBe user.email
                verify(userRepository).create(user)
            }

        }
    }

})
