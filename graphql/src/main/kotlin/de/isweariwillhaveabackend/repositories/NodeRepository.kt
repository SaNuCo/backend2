package de.isweariwillhaveabackend.repositories

import com.expediagroup.graphql.generator.scalars.ID
import io.github.graphglue.model.Node
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NodeRepository : ReactiveCrudRepository<Node, String>

suspend fun <T> ReactiveCrudRepository<T, String>.findById(id: ID) = this.findById(id.value).awaitSingle()