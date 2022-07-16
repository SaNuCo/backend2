package de.isweariwillhaveabackend.repositories

import de.isweariwillhaveabackend.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveCrudRepository<User, String> {
    suspend fun findUserByUsername(username: String): User?
}