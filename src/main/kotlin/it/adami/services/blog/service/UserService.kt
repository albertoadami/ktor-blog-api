package it.adami.services.blog.service

import it.adami.services.blog.exceptions.EmailAlreadyInUseException
import it.adami.services.blog.exceptions.UserNotFoundException
import it.adami.services.blog.hashing.BCryptHashingData
import it.adami.services.blog.model.User
import it.adami.services.blog.repository.UserRepository

interface UserService {

    suspend fun create(user: User): Long

    suspend fun getById(id: Long): User?

    suspend fun deleteById(id: Long)

    suspend fun login(email: String, password: String): User?
}

class UserServiceRules(private val userRepository: UserRepository): UserService {

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


}