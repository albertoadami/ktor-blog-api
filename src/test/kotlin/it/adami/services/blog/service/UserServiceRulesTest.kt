package it.adami.services.blog.service

import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.repository.UserRepository
import org.mockito.kotlin.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceRulesTest {

    private val userRepository: UserRepository = mock()
    private val userService: UserService = UserServiceRules(userRepository)

    @Test
    fun `create should return user ID when user is created successfully`() {
        // Arrange
        val user = User(name = "John", surname = "Doe", email = "john.doe@example.com", password = "password", status = UserStatus.PENDING, createdAt = Instant.now(), updatedAt = Instant.now())
        val expectedId = 1L
        whenever(userRepository.create(user)).thenReturn(expectedId)

        // Act
        val result = userService.create(user)

        // Assert
        assertEquals(expectedId, result)
        verify(userRepository).create(user)
    }

    @Test
    fun `create should return null when user creation fails`() {
        // Arrange
        val user = User(name = "John", surname = "Doe", email = "john.doe@example.com", password = "password", status = UserStatus.PENDING, createdAt = Instant.now(), updatedAt = Instant.now())
        whenever(userRepository.create(user)).thenReturn(null)

        // Act
        val result = userService.create(user)

        // Assert
        assertEquals(null, result)
        verify(userRepository).create(user)
    }
}
