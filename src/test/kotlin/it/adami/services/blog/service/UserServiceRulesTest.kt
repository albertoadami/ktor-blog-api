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

    class Fixtures {
        val userRepository: UserRepository = mock()
        val userService: UserService = UserServiceRules(userRepository)
    }

    "UserServiceRules.create()" should {

        "return the user ID when is created successfully" {
            val f = Fixtures()
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
                whenever(f.userRepository.create(user)).thenReturn(expectedId)

                // Act
                val result = f.userService.create(user)

                // Assert
                expectedId shouldBe result
                verify(f.userRepository).create(user)
            }

        }

        "throw EmailAlreadyInUseException when the email is already in use" {
            val f = Fixtures()
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
                whenever(f.userRepository.create(user)).thenReturn(null)

                // Act
                val exception = shouldThrow<EmailAlreadyInUseException> {
                    f.userService.create(user)
                }
                // Assert
                exception.email shouldBe user.email
                verify(f.userRepository).create(user)
            }

        }
    }

    "UserServiceRules.getById(id)" should {
        "return the user if the persistence layer is returning it" {
            val f = Fixtures()
            runBlocking {
                // Arrange
                val user = User(
                    id = 1,
                    name = "John",
                    surname = "Doe",
                    email = "john.doe@example.com",
                    password = "password",
                    status = UserStatus.PENDING,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                val expectedId = 1L
                whenever(f.userRepository.getById(user.id)).thenReturn(user)

                // Act
                val result = f.userService.getById(user.id)

                // Assert
                result shouldBe user
                verify(f.userRepository).getById(user.id)
            }
        }

        "return null if the persistence layer is not finding the user" {
            val f = Fixtures()

            whenever(f.userRepository.getById(1L)).thenReturn(null)

            val result = f.userService.getById(1L)

            result shouldBe null
            verify(f.userRepository).getById(1L)
        }
    }



})
