package it.adami.services.blog.service

import it.adami.services.blog.exceptions.EmailAlreadyInUseException
import it.adami.services.blog.model.User
import it.adami.services.blog.repository.UserRepository

interface UserService {

    suspend fun create(user: User): Long

    suspend fun getById(id: Long): User?
}

class UserServiceRules(private val userRepository: UserRepository): UserService {

    override suspend fun create(user: User): Long {
        return userRepository.create(user) ?: throw EmailAlreadyInUseException(user.email)
    }

    override suspend fun getById(id: Long): User? {
        return userRepository.getById(id)
    }


}