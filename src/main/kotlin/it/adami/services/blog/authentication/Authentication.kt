package it.adami.services.blog.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import it.adami.services.blog.model.UserStatus
import java.util.Date

interface Authentication {

    fun generateToken(email: String, userId: Long, userStatus: UserStatus): String

}

class JWTAuthentication(private val secretKey: String, private val duration: Long) : Authentication {
    override fun generateToken(email: String, userId: Long, userStatus: UserStatus): String {
        val algorithm = Algorithm.HMAC256(secretKey)
        return JWT.create()
            .withAudience("blog-api-clients")
            .withIssuer("blog-api")
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("status", userStatus.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + duration))
            .sign(algorithm)
    }

}
