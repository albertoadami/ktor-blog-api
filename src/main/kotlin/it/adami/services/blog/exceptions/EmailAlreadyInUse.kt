package it.adami.services.blog.exceptions

class EmailAlreadyInUse(val email: String): RuntimeException("The $email is already in use")