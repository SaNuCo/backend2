package de.isweariwillhaveabackend.schema

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class Query : com.expediagroup.graphql.server.operations.Query {
    fun echo(input: String) = input
}