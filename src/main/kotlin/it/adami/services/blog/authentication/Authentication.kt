package it.adami.services.blog.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import it.adami.services.blog.config.JWTConfig
import it.adami.services.blog.model.UserStatus
import java.util.Date

class TokenInfo(val userId: Long, val email: String, val userStatus: UserStatus)

interface Authentication {

    fun generateToken(email: String, userId: Long, userStatus: UserStatus): String

    fun extractTokenInfo(token: JWTPrincipal): TokenInfo?

}

class JWTAuthentication(private val jwtConfig: JWTConfig) : Authentication {
    override fun generateToken(email: String, userId: Long, userStatus: UserStatus): String {
        val algorithm = Algorithm.HMAC256(jwtConfig.secretKey)
        return JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("status", userStatus.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.duration))
            .sign(algorithm)
    }

    override fun extractTokenInfo(token: JWTPrincipal): TokenInfo? {

        val userId = token.getClaim("userId", Long::class)
        val email = token.getClaim("email", String::class)
        val userStatus = token.getClaim("status", String::class)?.let { UserStatus.valueOf(it) }

        if(userId == null || email == null || userStatus == null)
            return  null



        return TokenInfo(userId, email, userStatus)
    }

}
