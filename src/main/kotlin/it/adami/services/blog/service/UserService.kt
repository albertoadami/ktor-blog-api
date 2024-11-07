package it.adami.services.blog.service

import it.adami.services.blog.model.User

interface UserService {

    fun create(user: User): Long?
}

class InMemoryUserService: UserService {

    private var users = mutableMapOf<Long, User>()
    private var nextId: Long = 1L

    override fun create(user: User): Long? {
        val newId = nextId
        users[newId] = user.copy(id = newId)
        nextId+=1

        return nextId
    }


}