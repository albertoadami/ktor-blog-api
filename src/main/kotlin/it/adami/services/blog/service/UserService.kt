package it.adami.services.blog.service

import it.adami.services.blog.model.User
import it.adami.services.blog.repository.UserRepository

interface UserService {

    fun create(user: User): Long?
}

class UserServiceRules(private val userRepository: UserRepository): UserService {

    private var users = mutableMapOf<Long, User>()
    private var nextId: Long = 1L

    override fun create(user: User): Long? {
        return userRepository.create(user)
    }


}