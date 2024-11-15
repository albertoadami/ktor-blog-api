package it.adami.services.blog.repository

import it.adami.services.blog.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.JavaInstantColumnType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

interface PostRepository {

    suspend fun create(post: Post): Long

    suspend fun getById(id: Long): Post?
}


class ExposedPostRepository : PostRepository {

    private object Posts: Table("posts") {
        val id = long("id").autoIncrement()
        val title = varchar("title", 255)
        val text = text("text")
        val authorId = long("author_id").references(ExposedUserRepository.Users.id)
        val upVotes = integer("up_votes")
        val downVotes = integer("down_votes")
        val createdAt = registerColumn<Instant>("created_at", JavaInstantColumnType())
        val updatedAt = registerColumn<Instant>("updated_at", JavaInstantColumnType())

        override val primaryKey = PrimaryKey(id)
    }


    override suspend fun create(post: Post): Long {
        return transaction {
                Posts.insert {
                    it[title] = post.title
                    it[text] = post.text
                    it[authorId] = post.authorId
                    it[upVotes] = post.upVotes
                    it[downVotes] = post.downVotes
                    it[createdAt] = post.createdAt
                    it[updatedAt] = post.updatedAt
                }[Posts.id]
            }

    }

    override suspend fun getById(id: Long): Post? = withContext(Dispatchers.IO){
        transaction{
            Posts.select { Posts.id eq id }
                .mapNotNull{toPost(it)}
                .singleOrNull()

        }
    }

    private fun toPost(row: ResultRow): Post {
        return Post(
            id = row[Posts.id],
            title = row[Posts.title],
            text = row[Posts.text],
            authorId = row[Posts.authorId],
            upVotes = row[Posts.upVotes],
            downVotes = row[Posts.downVotes],
            createdAt = row[Posts.createdAt],
            updatedAt = row[Posts.updatedAt]
        )
    }

}