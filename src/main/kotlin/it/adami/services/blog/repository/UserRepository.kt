package it.adami.services.blog.repository

import it.adami.services.blog.model.User

interface UserRepository {

    fun create(user: User): Long?
}

class InMemoryUserRepository: UserRepository {

    private var users = mutableMapOf<Long, User>()
    private var nextId = 1L

    override fun create(user: User): Long? {
        val newId = nextId
        users[newId] = user.copy(id = newId)
        nextId+=1

        return newId
    }

}