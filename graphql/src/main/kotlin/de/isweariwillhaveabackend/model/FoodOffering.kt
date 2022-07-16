package de.isweariwillhaveabackend.model

import io.github.graphglue.model.*
import org.springframework.data.annotation.Transient

@DomainNode("foodOfferings")
class FoodOffering(
    var title: String,
    var description: String,
    var picture: String?,
    var location: List<Double>,
    var category: String
) : Node() {

    companion object {
        const val REACTION = "REACTION"
        const val CHAT = "CHAT"
    }

    @NodeRelationship(User.FOOD_OFFERING, Direction.INCOMING)
    @FilterProperty
    @delegate:Transient
    val offeredBy by NodeProperty<User>()

    @NodeRelationship(REACTION, Direction.OUTGOING)
    @FilterProperty
    @delegate:Transient
    val reactions by NodeSetProperty<FoodOfferingReaction>()

    @NodeRelationship(CHAT, Direction.OUTGOING)
    @delegate:Transient
    val chats by NodeSetProperty<Chat>()
}