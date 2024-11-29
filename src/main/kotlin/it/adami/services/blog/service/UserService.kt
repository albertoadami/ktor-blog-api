package it.adami.services.blog.service

import ch.qos.logback.classic.Logger
import it.adami.services.blog.exceptions.EmailAlreadyInUseException
import it.adami.services.blog.exceptions.UserInInvalidStateException
import it.adami.services.blog.exceptions.UserNotFoundException
import it.adami.services.blog.hashing.BCryptHashingData
import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.repository.UserRepository
import org.slf4j.LoggerFactory
import java.time.Instant

interface UserService {

    suspend fun create(user: User): Long

    suspend fun getById(id: Long): User?

    suspend fun deleteById(id: Long)

    suspend fun login(email: String, password: String): User?

    suspend fun activateUser(id: Long)
}

class UserServiceRules(private val userRepository: UserRepository): UserService {

    private val logger = LoggerFactory.getLogger(UserServiceRules::class.java)

    override suspend fun create(user: User): Long {
        return userRepository.create(user) ?: throw EmailAlreadyInUseException(user.email)
    }

    override suspend fun getById(id: Long): User? {
        return userRepository.getById(id)
    }

    override suspend fun deleteById(id: Long) {
        val result = userRepository.deleteById(id)
        if(!result) throw UserNotFoundException(id)
    }

    override suspend fun login(email: String, password: String): User? {
        val user = userRepository.getByEmail(email) ?: return null
        return if (BCryptHashingData.verify(password, user.password)) user else null
    }

    override suspend fun activateUser(id: Long) {
        val user = userRepository.getById(id) ?: throw UserNotFoundException(id)

        // Check user status
        if (user.isActive) {
            val errorMessage = "User $id is in an invalid state for activation. Actual: ${user.status}, required: ${UserStatus.PENDING}"
            logger.warn(errorMessage)
            throw UserInInvalidStateException(id, user.status)
        }

        // Prepare updated user
        val updatedUser = user.copy(status = UserStatus.ENABLED, updatedAt = Instant.now())

        // Perform the update
        val updateResult = userRepository.update(updatedUser)

        if (!updateResult) {
            val warningMessage = "Activation of user $id failed; no rows were updated."
            logger.warn(warningMessage)
            return
        }

        logger.info("User $id successfully activated.")
    }



}