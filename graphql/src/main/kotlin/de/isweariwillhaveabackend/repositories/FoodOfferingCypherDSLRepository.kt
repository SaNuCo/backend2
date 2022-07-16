package de.isweariwillhaveabackend.repositories

import de.isweariwillhaveabackend.model.FoodOffering
import org.springframework.data.neo4j.repository.support.ReactiveCypherdslStatementExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoodOfferingCypherDSLRepository : ReactiveCrudRepository<FoodOffering, String>, ReactiveCypherdslStatementExecutor<FoodOffering> {
}