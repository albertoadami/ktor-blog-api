package it.adami.services.blog.service

import it.adami.services.blog.exceptions.EmailAlreadyInUse
import it.adami.services.blog.model.User
import it.adami.services.blog.repository.UserRepository

interface UserService {

    suspend fun create(user: User): Long
}

class UserServiceRules(private val userRepository: UserRepository): UserService {

    override suspend fun create(user: User): Long {
        return userRepository.create(user) ?: throw EmailAlreadyInUse(user.email)
    }


}