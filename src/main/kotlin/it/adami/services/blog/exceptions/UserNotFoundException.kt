package it.adami.services.blog.exceptions

class UserNotFoundException(val id: Long) : NoSuchElementException("User $id is not found")