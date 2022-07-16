package de.isweariwillhaveabackend.repositories

import de.isweariwillhaveabackend.model.FoodOffering
import org.springframework.data.neo4j.repository.support.ReactiveCypherdslStatementExecutor
import org.springframework.stereotype.Repository

@Repository
interface FoodOfferingCypherDSLRepository : ReactiveCypherdslStatementExecutor<FoodOffering> {
}