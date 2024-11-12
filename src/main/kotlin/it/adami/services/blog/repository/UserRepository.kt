package it.adami.services.blog.repository

import it.adami.services.blog.model.User
import it.adami.services.blog.model.UserStatus
import it.adami.services.blog.util.PGEnumColumnType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.JavaInstantColumnType
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

interface UserRepository {

    suspend fun create(user: User): Long?

    suspend fun getById(id: Long): User?
}

class ExposedUserRepository: UserRepository {

    private object Users : Table("users") {
        val id = long("id").autoIncrement()
        val name = varchar("name", 255)
        val surname = varchar("surname", 255)
        val email = varchar("email", 255)
        val password = varchar("password", 255)
        val status = registerColumn<UserStatus>("status", PGEnumColumnType("user_status", UserStatus::class.java))
        val createdAt = registerColumn<Instant>("created_at", JavaInstantColumnType())
        val updatedAt = registerColumn<Instant>("updated_at", JavaInstantColumnType())

        override val primaryKey = PrimaryKey(id)
    }

    override suspend fun create(user: User): Long? = withContext(Dispatchers.IO) {
        try {
            transaction {
                val result = Users.insert {
                    it[name] = user.name
                    it[surname] = user.surname
                    it[email] = user.email
                    it[password] = user.password
                    it[status] = user.status
                    it[createdAt] = user.createdAt
                    it[updatedAt] = user.updatedAt
                }
                return@transaction result[Users.id]
            }
        } catch (e: ExposedSQLException) {
            if (e.message?.contains("users_email_key") == true) {
                null
            } else {
                throw e
            }
        }
    }

    override suspend fun getById(id: Long): User? = withContext(Dispatchers.IO) {
        transaction {
            Users
                .select( Users.id eq id)
                .mapNotNull{toUser(it)}
                .singleOrNull()
        }
    }

    private fun toUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            name = row[Users.name],
            surname = row[Users.surname],
            email = row[Users.email],
            password = row[Users.password],
            status = row[Users.status],
            createdAt = row[Users.createdAt],
            updatedAt = row[Users.updatedAt]
        )
    }

}
