package de.isweariwillhaveabackend.model

import io.github.graphglue.model.*
import org.springframework.data.annotation.Transient

@DomainNode("users")
class User(
    @FilterProperty
    val username: String,
    var displayName: String,
    var isOnline: Boolean,
    var avatar: String?
) : Node() {

    companion object {
        const val CHAT = "CHAT"
        const val FOOD_OFFERING = "FOOD_OFFERING"
        const val FOOD_OFFERING_REACTION = "FOOD_OFFERING_REACTION"
    }

    @NodeRelationship(FOOD_OFFERING, Direction.OUTGOING)
    @delegate:Transient
    val foodOfferings by NodeSetProperty<FoodOffering>()

    @NodeRelationship(FOOD_OFFERING_REACTION, Direction.OUTGOING)
    @delegate:Transient
    val foodOfferingReactions by NodeSetProperty<FoodOfferingReaction>()
}