package it.adami.services.blog.exceptions

class EmailAlreadyInUseException(val email: String): RuntimeException("The $email is already in use")