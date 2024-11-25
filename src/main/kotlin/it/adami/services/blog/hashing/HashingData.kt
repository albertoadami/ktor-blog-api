package it.adami.services.blog.hashing

import org.mindrot.jbcrypt.BCrypt

object BCryptHashingData  {
    fun encrypt(data: String): String =
        BCrypt.hashpw(data, BCrypt.gensalt())

    fun verify(plainText: String, hashed: String): Boolean =
        BCrypt.checkpw(plainText, hashed)

}