package de.isweariwillhaveabackend.schema

import com.expediagroup.graphql.generator.scalars.ID
import de.isweariwillhaveabackend.graphql.user
import de.isweariwillhaveabackend.model.FoodOffering
import de.isweariwillhaveabackend.model.FoodOfferingReaction
import de.isweariwillhaveabackend.model.User
import de.isweariwillhaveabackend.repositories.FoodOfferingCypherDSLRepository
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.reactor.awaitSingle
import org.neo4j.cypherdsl.core.Conditions
import org.neo4j.cypherdsl.core.Cypher
import org.neo4j.cypherdsl.core.Functions
import org.neo4j.cypherdsl.core.renderer.Renderer
import org.springframework.data.neo4j.core.ReactiveNeo4jClient
import org.springframework.stereotype.Component

@Component
class Query(val foodOfferingRepository: FoodOfferingCypherDSLRepository, val client: ReactiveNeo4jClient) :
    com.expediagroup.graphql.server.operations.Query {
    fun echo(input: String) = input

    suspend fun foodOfferingsNear(
        location: List<Double>,
        dfe: DataFetchingEnvironment,
        after: ID?,
        count: Int
    ): List<FoodOffering> {
        val node = Cypher.node(FoodOffering::class.simpleName)
        val userId = Cypher.anonParameter(dfe.user.rawId)
        val long = Cypher.anonParameter(location[0])
        val lat = Cypher.anonParameter(location[1])
        val lastId = Cypher.anonParameter(after?.value)
        val limit = if (after != null) {
            val lastNode = Cypher.node(FoodOffering::class.simpleName).withProperties("id", lastId)
            val statement = Cypher.match(lastNode).returning(
                Functions.distance(
                    Functions.point(Cypher.mapOf("longitude", long, "latitude", lat)),
                    Functions.point(
                        Cypher.mapOf(
                            "longitude",
                            Cypher.valueAt(lastNode.property("location"), 0),
                            "latitude",
                            Cypher.valueAt(lastNode.property("location"), 1)
                        )
                    )
                )
            ).build()
            println(Renderer.getDefaultRenderer().render(statement))
            client.query(Renderer.getDefaultRenderer().render(statement)).bindAll(statement.parameters)
                .fetchAs(Double::class.java).first().awaitSingle()
        } else {
            0
        }
        println(limit)
        val dist = Cypher.name("dist")
        val firstPart = Cypher.match(node).with(node).where(
            Conditions.not(
                node.relationshipFrom(
                    Cypher.node(User::class.simpleName).withProperties(mapOf("id" to userId)), User.FOOD_OFFERING
                )
            ).and(
                Conditions.not(
                    node.relationshipTo(
                        Cypher.node(FoodOfferingReaction::class.simpleName),
                        FoodOffering.REACTION
                    ).relationshipFrom(
                        Cypher.node(User::class.simpleName).withProperties(mapOf("id" to userId)),
                        User.FOOD_OFFERING_REACTION
                    )
                )
            )
        ).with(
            node, Functions.distance(
                Functions.point(Cypher.mapOf("longitude", long, "latitude", lat)),
                Functions.point(
                    Cypher.mapOf(
                        "longitude",
                        Cypher.valueAt(node.property("location"), 0),
                        "latitude",
                        Cypher.valueAt(node.property("location"), 1)
                    )
                )
            ).`as`(dist)
        )
        val secondPart = if (after != null) {
            firstPart.where(dist.gte(Cypher.anonParameter(limit)).and(node.property("id").ne(lastId))).with(node, dist)
        } else {
            firstPart
        }
        val statement = secondPart.orderBy(dist).ascending().limit(Cypher.anonParameter(count)).returning(node).build()
        return foodOfferingRepository.findAll(statement).collectList().awaitSingle()
    }
}